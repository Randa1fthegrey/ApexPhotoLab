package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers

import android.graphics.Color
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure.ColorGroupSorter

/**
 * Job: Multicolor Blob Detector (The Scanner).
 * Responsibility: Scanning the pixel array for connected regions via flood-fill and coordinating validation and edge calculation.
 */
object MulticolorBlobDetector {

    fun detect(pixels: IntArray, width: Int, height: Int): List<MulticolorBlob> {
        val visited = BooleanArray(pixels.size)
        val blobs = mutableListOf<MulticolorBlob>()

        for (startIdx in pixels.indices) {
            if (visited[startIdx]) continue
            visited[startIdx] = true
            if (Color.alpha(pixels[startIdx]) < ALPHA_THRESHOLD) continue

            val blobIndices = mutableSetOf<Int>()
            val colorGroupIds = mutableSetOf<Int>()
            val queue = ArrayDeque<Int>()
            queue.add(startIdx)

            while (queue.isNotEmpty()) {
                val idx = queue.removeFirst()
                blobIndices.add(idx)
                colorGroupIds.add(ColorGroupSorter.getGroupIndexForPixel(pixels[idx]))

                // Early Exit for background blobs (performance optimization)
                if (blobIndices.size > MAX_INTERNAL_BLOB_SIZE) break

                val x = idx % width
                val y = idx / width

                for ((dx, dy) in NEIGHBOR_OFFSETS) {
                    val nx = x + dx
                    val ny = y + dy
                    if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue
                    val ni = ny * width + nx
                    if (visited[ni]) continue
                    visited[ni] = true
                    if (Color.alpha(pixels[ni]) >= ALPHA_THRESHOLD) queue.add(ni)
                }
            }

            // 1. VALIDATION
            if (MulticolorBlobValidator.isValid(colorGroupIds, blobIndices.size)) {
                // 2. EDGE CALCULATION
                val edgePoints = MulticolorEdgeCalculator.calculate(blobIndices, width, height)
                blobs.add(MulticolorBlob(blobIndices, colorGroupIds, edgePoints))
            }
        }

        return blobs
    }

    private val NEIGHBOR_OFFSETS = listOf(Pair(0, -1), Pair(1, 0), Pair(0, 1), Pair(-1, 0))
    private const val ALPHA_THRESHOLD = 1
    private const val MAX_INTERNAL_BLOB_SIZE = 100000
}