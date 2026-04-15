package com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift.consolidators

import android.graphics.Point
import android.graphics.Rect
import com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.ColorGroupSorter

/**
 * A specialist for the Third Shift.
 * Consolidates fragmented paths into logical, high-fidelity elements.
 */
object ThirdShiftConsolidator {

    /**
     * Merges fragments that belong to the same logical shape based on color and proximity.
     */
    fun consolidate(
        paths: List<List<Point>>,
        colors: List<Int>
    ): Pair<List<List<Point>>, List<Int>> {
        if (paths.size <= 1) return Pair(paths, colors)

        val consolidatedPaths = mutableListOf<List<Point>>()
        val consolidatedColors = mutableListOf<Int>()
        val usedIndices = mutableSetOf<Int>()

        for (i in paths.indices) {
            if (i in usedIndices) continue

            val currentPath = paths[i].toMutableList()
            val currentColor = colors[i]
            val currentGroup = ColorGroupSorter.getGroupIndexForPixel(currentColor)
            val currentBounds = getBoundingBox(paths[i])

            usedIndices.add(i)

            // Look for "Neighbors" in the same color family
            for (j in i + 1 until paths.size) {
                if (j in usedIndices) continue

                val nextColor = colors[j]
                val nextGroup = ColorGroupSorter.getGroupIndexForPixel(nextColor)

                if (currentGroup == nextGroup) {
                    val nextBounds = getBoundingBox(paths[j])
                    // If they are spatially close or overlapping, they are likely the same shape
                    if (isSpatiallyRelated(currentBounds, nextBounds)) {
                        // Weld using sentinel
                        currentPath.add(Point(-1, -1))
                        currentPath.addAll(paths[j])
                        usedIndices.add(j)
                    }
                }
            }

            consolidatedPaths.add(currentPath)
            consolidatedColors.add(currentColor)
        }

        return Pair(consolidatedPaths, consolidatedColors)
    }

    private fun getBoundingBox(path: List<Point>): Rect {
        if (path.isEmpty()) return Rect()
        var minX = Int.MAX_VALUE; var minY = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE; var maxY = Int.MIN_VALUE
        for (p in path) {
            if (p.x == -1) continue
            if (p.x < minX) minX = p.x
            if (p.x > maxX) maxX = p.x
            if (p.y < minY) minY = p.y
            if (p.y > maxY) maxY = p.y
        }
        return Rect(minX, minY, maxX, maxY)
    }

    private fun isSpatiallyRelated(r1: Rect, r2: Rect): Boolean {
        // Expand boxes slightly to detect near-neighbors
        val buffer = 5
        val er1 = Rect(r1.left - buffer, r1.top - buffer, r1.right + buffer, r1.bottom + buffer)
        return er1.intersects(r2.left, r2.top, r2.right, r2.bottom)
    }
}