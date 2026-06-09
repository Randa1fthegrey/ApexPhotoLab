package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers

import android.graphics.Point

/**
 * Job: Multicolor Edge Calculator.
 * Responsibility: Calculating the list of boundary points for a connected pixel region.
 */
object MulticolorEdgeCalculator {

    private val NEIGHBOR_OFFSETS = listOf(Pair(0, -1), Pair(1, 0), Pair(0, 1), Pair(-1, 0))

    fun calculate(blobIndices: Set<Int>, width: Int, height: Int): Set<Point> {
        val edgePoints = mutableSetOf<Point>()
        for (idx in blobIndices) {
            val x = idx % width
            val y = idx / width
            var isEdge = false
            for ((dx, dy) in NEIGHBOR_OFFSETS) {
                val nx = x + dx
                val ny = y + dy
                if (nx < 0 || nx >= width || ny < 0 || ny >= height) {
                    isEdge = true
                    break
                }
                if (!blobIndices.contains(ny * width + nx)) {
                    isEdge = true
                    break
                }
            }
            if (isEdge) edgePoints.add(Point(x, y))
        }
        return edgePoints
    }
}