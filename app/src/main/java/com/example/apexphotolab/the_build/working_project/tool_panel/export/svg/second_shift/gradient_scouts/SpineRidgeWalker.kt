package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Point

/**
 * Job: Spine Ridge Walker.
 * Responsibility: Executing the "Ridge Walk" algorithm to discover intensity spines across the score map.
 */
object SpineRidgeWalker {

    fun walk(
        width: Int,
        height: Int,
        targetPoints: List<Int>,
        scores: FloatArray
    ): List<List<Point>> {
        val allSpines = mutableListOf<List<Point>>()
        val visited = BooleanArray(width * height)

        // Sort starting points by score to start at the strongest parts of the gradient
        val seeds = targetPoints.filter { scores[it] > 0 }.sortedByDescending { scores[it] }

        for (seedIdx in seeds) {
            if (visited[seedIdx]) continue

            val spine = mutableListOf<Point>()
            var currentIdx = seedIdx

            while (currentIdx != -1) {
                val cx = currentIdx % width
                val cy = currentIdx / width
                spine.add(Point(cx, cy))
                visited[currentIdx] = true

                // Look for the best neighbor that hasn't been visited and is in the shape
                currentIdx = findBestNeighbor(currentIdx, width, height, scores, visited)
            }

            if (spine.size > 5) { // Only keep significant rails
                allSpines.add(spine)
            }
        }

        return allSpines
    }

    private fun findBestNeighbor(
        idx: Int,
        w: Int,
        h: Int,
        scores: FloatArray,
        visited: BooleanArray
    ): Int {
        val cx = idx % w
        val cy = idx / w

        var bestIdx = -1
        var maxScore = -1f

        // Check 8 neighbors
        for (dy in -1..1) {
            for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                val nx = cx + dx
                val ny = cy + dy

                if (nx in 0 until w && ny in 0 until h) {
                    val nIdx = ny * w + nx
                    if (!visited[nIdx] && scores[nIdx] > maxScore) {
                        maxScore = scores[nIdx]
                        bestIdx = nIdx
                    }
                }
            }
        }
        return bestIdx
    }
}
