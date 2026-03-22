package com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist

import android.graphics.Point

/**
 * A specialist tool for the Artist Phase.
 * Converts an ordered list of points into an SVG path data string (d="...").
 * High-Fidelity Update: Surgically handles segment closure to prevent diagonal jumps.
 */
object PathDataGenerator {

    /**
     * Generates an SVG path data string and indicates if it was closed.
     * Supports Point(-1, -1) as a MoveTo sentinel.
     */
    fun generateWithStatus(path: List<Point>): Pair<String, Boolean> {
        if (path.isEmpty()) return Pair("", false)

        val pathData = StringBuilder()
        var isFirstPointInSegment = true
        var segmentCount = 0
        
        val hasSentinels = path.any { it.x == -1 }
        // Simplification is only safe for simple paths without sentinels
        val points = if (hasSentinels) path else simplify(path)

        for (i in points.indices) {
            val p = points[i]
            if (p.x == -1 && p.y == -1) {
                // Sentinel found: Force close the current segment to prevent chords
                if (segmentCount > 2) {
                    pathData.append("Z ")
                }
                isFirstPointInSegment = true
                segmentCount = 0
                continue
            }

            if (isFirstPointInSegment) {
                pathData.append("M ${p.x} ${p.y} ")
                isFirstPointInSegment = false
            } else {
                pathData.append("L ${p.x} ${p.y} ")
            }
            segmentCount++
        }

        // Final segment check: Force close the last part of the path
        if (segmentCount > 2) {
            pathData.append("Z")
        }

        // COMPOUND PATH LOGIC:
        // We report true if it's a compound path or if we added at least one Z.
        val isClosed = hasSentinels || pathData.contains("Z")

        return Pair(pathData.toString().trim(), isClosed)
    }

    fun generate(path: List<Point>): String = generateWithStatus(path).first

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
            val crossProduct = dy1 * dx2 - dy2 * dx1
            val dotProduct = dx1 * dx2 + dy1 * dy2
            if (crossProduct != 0 || dotProduct < 0) simplified.add(curr)
        }
        simplified.add(path.last())
        return simplified
    }
}
