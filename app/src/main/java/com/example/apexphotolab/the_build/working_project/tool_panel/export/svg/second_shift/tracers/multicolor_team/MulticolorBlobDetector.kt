package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.multicolor_team

import android.graphics.Color
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.ColorGroupSorter

/**
 * Job: Multicolor Blob Detector.
 * Responsibility: Finding spatially connected pixel regions that span two or more color groups, flagging them as multi-color shapes requiring unified boundary tracing.
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

            var isTooBig = false

            while (queue.isNotEmpty()) {
                val idx = queue.removeFirst()
                
                if (!isTooBig) {
                    blobIndices.add(idx)
                    colorGroupIds.add(ColorGroupSorter.getGroupIndexForPixel(pixels[idx]))
                    if (blobIndices.size > MAX_BLOB_SIZE) {
                        isTooBig = true
                    }
                }

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

            if (isTooBig || colorGroupIds.size < MIN_COLOR_GROUPS || blobIndices.size < MIN_BLOB_SIZE) continue

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

            blobs.add(MulticolorBlob(blobIndices, colorGroupIds, edgePoints))
        }

        return blobs
    }

    private val NEIGHBOR_OFFSETS = listOf(Pair(0, -1), Pair(1, 0), Pair(0, 1), Pair(-1, 0))

    private const val ALPHA_THRESHOLD = 1 // Process anything not 100% transparent
    private const val MIN_COLOR_GROUPS = 2
    private const val MIN_BLOB_SIZE = 50
    private const val MAX_BLOB_SIZE = 100000 // Background protection
}