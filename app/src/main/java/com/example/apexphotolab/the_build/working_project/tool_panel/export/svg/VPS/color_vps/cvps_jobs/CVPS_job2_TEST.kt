package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.CVPS_Audit
import java.nio.ByteBuffer
import kotlin.math.abs

/**
 * Job: CVPS Job 2 - TEST Discovery (Exhaustive Vacuum Tracer).
 * Responsibility: Consuming every pixel provided in the edge set to create continuous paths.
 * Uses a "West-Bias" (Left-turn) priority to maintain orderly movement along perimeters,
 * but no longer requires a "wall" to be present, ensuring 100% pixel coverage.
 */
object CVPS_job2_TEST {

    fun execute(colorId: Int, data: Any?) {
        val dData = data as? CVPS_job2.DiscoveryData ?: return
        val candidates = dData.specificCandidates ?: dData.edges.sortedBy { it.y * 10000 + it.x }
        
        dData.result = trace(colorId, candidates, dData.vram, dData.width, dData.height, dData.sharedRemainingSet)
    }

    private fun trace(
        colorId: Int,
        homeCandidates: List<Point>,
        vram: ByteBuffer,
        width: Int,
        height: Int,
        remainingPixels: MutableSet<Point>? = null
    ): List<List<Point>> {
        val allPaths = mutableListOf<List<Point>>()
        val myRemaining = remainingPixels ?: homeCandidates.toMutableSet()

        for (home in homeCandidates) {
            if (!myRemaining.contains(home)) continue

            val currentComponentPath = mutableListOf<Point>()
            val stack = mutableListOf<Point>()
            var currentPoint = home
            var currentDir = 0
            var pixelCount = 0
            var closedHome = false

            while (true) {
                // Consume the pixel and record to component path and stack
                if (myRemaining.remove(currentPoint)) {
                    currentComponentPath.add(currentPoint)
                    stack.add(currentPoint)
                    pixelCount++
                }

                // 1. PRIMARY WALK (Vacuum Priority - Bias Left)
                val stepResult = findNextStep(currentPoint, currentDir, myRemaining, home, currentComponentPath.size, vram, width, height)
                var nextPoint = stepResult.first
                var nextDir = stepResult.second

                // LOOP CLOSURE: If we hit home, we close the loop but don't stop vacuuming
                if (nextPoint == home && !closedHome && currentComponentPath.size > 3) {
                    currentComponentPath.add(home)
                    closedHome = true
                    nextPoint = null // Force recovery/backtrack to clean the rest of the ribbon
                }

                // 2. RADIUS RECOVERY (If immediate neighbors are gone, leap to the next closest)
                if (nextPoint == null) {
                    val recoveryResult = findRecoveryPoint(currentPoint, currentDir, myRemaining, vram, width, height)
                    nextPoint = recoveryResult.first
                    nextDir = recoveryResult.second
                }

                if (nextPoint != null) {
                    currentPoint = nextPoint
                    currentDir = nextDir
                } else {
                    // 3. STACK BACKTRACKING (Sentinel Mode)
                    var foundBranch = false
                    
                    while (stack.isNotEmpty()) {
                        val junction = stack.removeAt(stack.size - 1)
                        
                        // Search all 8 directions from the junction for any remaining pixel
                        for (i in 0 until 8) {
                            var branchResult = findNextStep(junction, i, myRemaining, home, 0, vram, width, height)
                            
                            // SYMMETRICAL RECOVERY: If immediate neighbors are gone, leap from the junction
                            if (branchResult.first == null) {
                                val recovery = findRecoveryPoint(junction, i, myRemaining, vram, width, height)
                                if (recovery.first != null) branchResult = recovery
                            }

                            if (branchResult.first != null) {
                                // Pave the gap with a sentinel to keep the pen down
                                currentComponentPath.add(val_util.SENTINEL)
                                currentComponentPath.add(junction) // Re-anchor at junction
                                
                                currentPoint = branchResult.first!!
                                currentDir = branchResult.second
                                foundBranch = true
                                break
                            }
                        }
                        if (foundBranch) break
                    }

                    if (!foundBranch) {
                        // All pixels in this connected component have been consumed
                        if (pixelCount > 2) {
                            allPaths.add(currentComponentPath)
                            CVPS_Audit.recordHit(colorId)
                        } else if (pixelCount > 0) {
                            CVPS_Audit.recordMiss(colorId)
                        }
                        break
                    }
                }
            }
        }
        return allPaths
    }

    private fun findNextStep(
        current: Point,
        dir: Int,
        remaining: Set<Point>,
        home: Point,
        pathSize: Int,
        vram: ByteBuffer,
        width: Int,
        height: Int
    ): Pair<Point?, Int> {
        for (rel in val_util.VACUUM_SEARCH_ORDER) {
            val targetDir = (dir + rel + 8) % 8
            val offset = val_util.OFFSETS[targetDir]
            val nx = current.x + offset.x
            val ny = current.y + offset.y

            if (nx !in 0 until width || ny !in 0 until height) continue
            
            val candidate = Point(nx, ny)
            if (candidate == home && pathSize > 3) return Pair(home, targetDir)

            if (remaining.contains(candidate) && getBit(vram, ny * width + nx)) {
                return Pair(candidate, targetDir)
            }
        }
        return Pair(null, dir)
    }

    private fun findRecoveryPoint(
        current: Point,
        dir: Int,
        remaining: Set<Point>,
        vram: ByteBuffer,
        width: Int,
        height: Int
    ): Pair<Point?, Int> {
        for (r in 1..val_util.RECOVERY_RADIUS) {
            for (dy in -r..r) {
                for (dx in -r..r) {
                    if (abs(dx) < r && abs(dy) < r) continue
                    val nx = current.x + dx
                    val ny = current.y + dy
                    if (nx !in 0 until width || ny !in 0 until height) continue
                    
                    val candidate = Point(nx, ny)
                    if (remaining.contains(candidate) && getBit(vram, ny * width + nx)) {
                        // Estimate direction based on vector from current to candidate
                        val newDir = estimateDirection(current, candidate)
                        return Pair(candidate, newDir)
                    }
                }
            }
        }
        return Pair(null, dir)
    }

    private fun estimateDirection(from: Point, to: Point): Int {
        val dx = to.x - from.x
        val dy = to.y - from.y
        // Normalize to -1, 0, 1
        val normX = if (dx == 0) 0 else dx / abs(dx)
        val normY = if (dy == 0) 0 else dy / abs(dy)
        
        return val_util.OFFSETS.indexOfFirst { it.x == normX && it.y == normY }.coerceAtLeast(0)
    }

    private fun getBit(buffer: ByteBuffer, index: Int): Boolean {
        val byteIdx = index / 8
        if (byteIdx >= buffer.capacity()) return false
        val bitIdx = index % 8
        return (buffer.get(byteIdx).toInt() and (1 shl bitIdx)) != 0
    }
}
