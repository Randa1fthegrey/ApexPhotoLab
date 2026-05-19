package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist

import android.graphics.Point

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

        val pathData = StringBuilder()
        var segmentCount = 0
        var isFirstPointInSegment = true

        val hasSentinels = path.any { it.x == -1 }
        val points = if (hasSentinels) path else simplify(path)

        for (p in points) {
            // Sentinel Handling: Segment Break
            if (p.x == -1 && p.y == -1) {
                if (segmentCount > 2) {
                    pathData.append("Z ")
                }
                isFirstPointInSegment = true
                segmentCount = 0
                continue
            }

            if (isFirstPointInSegment) {
                // Segment Start: Absolute Move
                pathData.append("M ${p.x} ${p.y} ")
                isFirstPointInSegment = false
            } else {
                // Segment Continuation: Absolute Line
                pathData.append("L ${p.x} ${p.y} ")
            }
            segmentCount++
        }

        // Final closure check
        if (segmentCount > 2) {
            pathData.append("Z")
        }

        val isClosed = hasSentinels || pathData.contains("Z")
        return Pair(pathData.toString().trim(), isClosed)
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
