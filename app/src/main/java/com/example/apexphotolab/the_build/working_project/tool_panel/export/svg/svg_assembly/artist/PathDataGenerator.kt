package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist

import android.graphics.Point
import kotlin.math.abs

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.val_util as global_val_util

/**
 * Job: Path Data Generator.
 * Responsibility: Converting an ordered list of points into a bulletproof SVG path string using absolute coordinates.
 */
object PathDataGenerator {

    /**
     * Generates path data and returns a Pair containing the string and a closure status.
     * Uses absolute 'M' and 'L' coordinates to ensure 100% geometric accuracy.
     */
    fun generateWithStatus(path: List<Point>): Pair<String, Boolean> {
        if (path.isEmpty()) return Pair("", false)

        // Split by sentinels, simplify each segment, and rejoin
        val segments = mutableListOf<List<Point>>()
        var currentSegment = mutableListOf<Point>()
        
        for (p in path) {
            if (p.x == global_val_util.SENTINEL.x && p.y == global_val_util.SENTINEL.y) {
                if (currentSegment.isNotEmpty()) {
                    segments.add(simplify(currentSegment))
                    currentSegment = mutableListOf()
                }
            } else {
                currentSegment.add(p)
            }
        }
        if (currentSegment.isNotEmpty()) {
            segments.add(simplify(currentSegment))
        }

        val pathData = StringBuilder()
        var totalPoints = 0
        var hasClosedSegment = false

        for (segment in segments) {
            if (segment.isEmpty()) continue
            
            // Segment Start
            pathData.append("M ${segment[0].x} ${segment[0].y} ")
            
            for (i in 1 until segment.size) {
                pathData.append("L ${segment[i].x} ${segment[i].y} ")
            }
            
            // Auto-close if segment is a loop
            if (segment.size > 2 && isLoop(segment)) {
                pathData.append("Z ")
                hasClosedSegment = true
            }
            
            totalPoints += segment.size
        }

        return Pair(pathData.toString().trim(), hasClosedSegment)
    }

    private fun isLoop(segment: List<Point>): Boolean {
        if (segment.size < 3) return false
        val first = segment.first()
        val last = segment.last()
        return abs(first.x - last.x) <= 1 && abs(first.y - last.y) <= 1
    }

    /**
     * Standard path data generation.
     */
    fun generate(path: List<Point>): String = generateWithStatus(path).first

    /**
     * Collinear Point Removal: Drops points that lie on a straight line between their neighbors.
     */
    private fun simplify(path: List<Point>): List<Point> {
        if (path.size <= 2) return path
        val simplified = mutableListOf<Point>()
        simplified.add(path[0])
        for (i in 1 until path.size - 1) {
            val prev = simplified.last()
            val curr = path[i]
            val next = path[i + 1]
            
            val dx1 = curr.x - prev.x
            val dy1 = curr.y - prev.y
            val dx2 = next.x - curr.x
            val dy2 = next.y - curr.y
            
            // Cross product check for collinearity
            val crossProduct = dy1 * dx2 - dy2 * dx1
            // Dot product check to ensure we aren't removing a 180-degree turn
            val dotProduct = dx1 * dx2 + dy1 * dy2
            
            if (crossProduct != 0 || dotProduct < 0) {
                simplified.add(curr)
            }
        }
        simplified.add(path.last())
        return simplified
    }
}
