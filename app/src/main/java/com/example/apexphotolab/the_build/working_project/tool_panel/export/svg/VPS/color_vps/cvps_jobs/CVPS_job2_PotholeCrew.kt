package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.territory.ColorWallScale
import java.nio.ByteBuffer
import kotlin.math.abs

/**
 * Job: CVPS Job 2 Specialist - Pothole Crew.
 * Responsibility: Bridging gaps by following the Shoreline (the boundary between Grey and White/Alpha).
 */
object CVPS_job2_PotholeCrew {

    fun execute(
        fragments: List<List<Point>>,
        vram: ByteBuffer,
        width: Int,
        height: Int,
        pixels: IntArray,
        colorId: Int
    ): Int {
        if (fragments.size < 2) return 0
        var bridgesBuilt = 0
        
        // We only try to bridge fragments that are reasonably close
        for (i in fragments.indices) {
            val tail = fragments[i].last()
            
            var nearestHead: Point? = null
            var minDist = 30.0
            
            for (j in fragments.indices) {
                if (i == j) continue
                val head = fragments[j].first()
                val d = dist(tail, head)
                if (d < minDist) {
                    minDist = d.toDouble()
                    nearestHead = head
                }
            }

            if (nearestHead != null) {
                if (patchShoreline(tail, nearestHead, vram, width, height, pixels, colorId)) {
                    bridgesBuilt++
                }
            }
        }
        return bridgesBuilt
    }

    private fun patchShoreline(start: Point, end: Point, vram: ByteBuffer, width: Int, height: Int, pixels: IntArray, colorId: Int): Boolean {
        var current = start
        var currentDir = estimateDirection(start, end)
        var safety = 0
        
        while (dist(current, end) > 1 && safety < 50) {
            safety++
            var moved = false
            
            // Search Order: Prioritize hugging the Abyss wall
            val searchOrder = intArrayOf(-2, -1, 0, 1, 2, 3, 4, -3)
            for (rel in searchOrder) {
                val testDir = (currentDir + rel + 8) % 8
                val nx = current.x + val_util.OFFSETS[testDir].x
                val ny = current.y + val_util.OFFSETS[testDir].y
                
                if (nx !in 0 until width || ny !in 0 until height) continue
                
                // SHORELINE RULE: The pixel to the left must be the Abyss (White/Alpha)
                if (isWall(current, testDir, width, height, pixels, colorId)) {
                    // Claim this shoreline pixel
                    CVPS_VRAM_Util.setBit(vram, ny * width + nx)
                    
                    // If we bumped into an existing fragment, bridge is done
                    if (CVPS_VRAM_Util.getBit(vram, ny * width + nx) && safety > 2) return true
                    
                    current = Point(nx, ny)
                    currentDir = testDir
                    moved = true
                    break
                }
            }
            if (!moved) break // Lost the shoreline, stop to prevent branches
        }
        return false
    }

    private fun isWall(current: Point, targetDir: Int, width: Int, height: Int, pixels: IntArray, colorId: Int): Boolean {
        val leftHandDir = (targetDir - 2 + 8) % 8
        val lx = current.x + val_util.OFFSETS[leftHandDir].x
        val ly = current.y + val_util.OFFSETS[leftHandDir].y
        
        if (lx !in 0 until width || ly !in 0 until height) return true
        
        val leftGroup = ColorWallScale.getTerritoryId(pixels[ly * width + lx])
        // The "Wall" is specifically the White (6) or Alpha (7) background
        return leftGroup == 6 || leftGroup == 7 || leftGroup != colorId
    }

    private fun estimateDirection(from: Point, to: Point): Int {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val normX = if (dx == 0) 0 else dx / (abs(dx).coerceAtLeast(1))
        val normY = if (dy == 0) 0 else dy / (abs(dy).coerceAtLeast(1))
        return val_util.OFFSETS.indexOfFirst { it.x == normX && it.y == normY }.coerceAtLeast(0)
    }

    private fun dist(p1: Point, p2: Point): Int = abs(p1.x - p2.x) + abs(p1.y - p2.y)
}
