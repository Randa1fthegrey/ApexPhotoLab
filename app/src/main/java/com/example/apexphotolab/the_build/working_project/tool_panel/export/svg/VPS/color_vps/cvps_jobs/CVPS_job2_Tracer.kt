package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.CVPS_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.SVG_Unified_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.territory.ColorWallScale
import java.nio.ByteBuffer

/**
 * Job: CVPS Job 2 Specialist - Final Tracer.
 * Responsibility: Executing high-precision Moore-Neighbor path tracing on a healed VRAM.
 */
object CVPS_job2_Tracer {

    fun execute(
        colorId: Int,
        vram: ByteBuffer,
        width: Int,
        height: Int,
        pixels: IntArray
    ): List<List<Point>> {
        val allPaths = mutableListOf<List<Point>>()
        val remaining = mutableSetOf<Point>()
        
        // Harvest the HEALED path from VRAM
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (CVPS_VRAM_Util.getBit(vram, y * width + x)) {
                    remaining.add(Point(x, y))
                }
            }
        }

        val sorted = remaining.toList().sortedBy { it.y * 10000 + it.x }
        for (home in sorted) {
            if (!remaining.contains(home)) continue
            val path = mutableListOf<Point>()
            var current = home
            var currentDir = 0
            
            while (true) {
                if (remaining.remove(current)) path.add(current)
                
                val step = findNextStep(current, currentDir, remaining, home, path.size, vram, width, height, pixels, colorId)
                
                if (step.first == home && path.size > 3) {
                    path.add(home)
                    allPaths.add(path)
                    CVPS_Audit.recordHit(colorId)
                    break
                }
                if (step.first != null) {
                    current = step.first!!
                    currentDir = step.second
                } else {
                    if (path.size > 2) {
                        allPaths.add(path)
                        CVPS_Audit.recordHit(colorId)
                    }
                    break
                }
            }
        }
        return allPaths
    }

    fun findNextStep(
        current: Point,
        dir: Int,
        remaining: MutableSet<Point>,
        home: Point,
        pathSize: Int,
        vram: ByteBuffer,
        width: Int,
        height: Int,
        pixels: IntArray,
        colorId: Int
    ): Pair<Point?, Int> {
        // CARDINAL LOCK: Prevents zig-zags by forcing straight moves first.
        val cardinalOrder = intArrayOf(0, 2, 4, 6)
        val diagonalOrder = intArrayOf(1, 3, 5, 7)

        for (targetDir in cardinalOrder) {
            val offset = val_util.OFFSETS[targetDir]
            val nx = current.x + offset.x
            val ny = current.y + offset.y
            if (nx !in 0 until width || ny !in 0 until height) continue
            val candidate = Point(nx, ny)
            if (candidate == home && pathSize > 3) return Pair(home, targetDir)
            if (remaining.contains(candidate) && CVPS_VRAM_Util.getBit(vram, ny * width + nx)) {
                return Pair(candidate, targetDir)
            }
        }
        for (targetDir in diagonalOrder) {
            val offset = val_util.OFFSETS[targetDir]
            val nx = current.x + offset.x
            val ny = current.y + offset.y
            if (nx !in 0 until width || ny !in 0 until height) continue
            val candidate = Point(nx, ny)
            if (candidate == home && pathSize > 3) return Pair(home, targetDir)
            if (remaining.contains(candidate) && CVPS_VRAM_Util.getBit(vram, ny * width + nx)) {
                return Pair(candidate, targetDir)
            }
        }

        return Pair(null, dir)
    }

    private fun hasAbyss(x: Int, y: Int, targetDir: Int, width: Int, height: Int, pixels: IntArray, colorId: Int): Boolean {
        val leftHandDir = (targetDir - 2 + 8) % 8
        val lx = x + val_util.OFFSETS[leftHandDir].x
        val ly = y + val_util.OFFSETS[leftHandDir].y
        
        return if (lx !in 0 until width || ly !in 0 until height) {
            true 
        } else {
            // THE ABYSS: Any pixel that is NOT part of our Grey territory
            ColorWallScale.getTerritoryId(pixels[ly * width + lx]) != colorId
        }
    }
}
