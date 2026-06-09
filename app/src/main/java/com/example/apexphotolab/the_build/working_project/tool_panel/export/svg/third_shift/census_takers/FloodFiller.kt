package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers

import android.graphics.Point
import kotlinx.coroutines.ensureActive
import java.util.LinkedList
import java.util.Queue
import kotlin.coroutines.coroutineContext

/**
 * Job: Flood Filler.
 * Responsibility: Performing a cancellable flood-fill to collect all interior
 * pixels of a shape that share the same color.
 */
object FloodFiller {

    suspend fun floodFill(
        startNode: Point,
        localPixels: IntArray,
        width: Int,
        height: Int
    ): List<Int> {
        if (startNode.y * width + startNode.x >= localPixels.size) {
            return emptyList()
        }
        val interiorPixels = mutableListOf<Int>()
        val targetColor = localPixels[startNode.y * width + startNode.x]
        val queue: Queue<Point> = LinkedList()
        val visited = BooleanArray(width * height)

        queue.add(startNode)
        visited[startNode.y * width + startNode.x] = true

        var iteration = 0
        while (queue.isNotEmpty()) {
            if (iteration++ % CANCELLATION_CHECK_INTERVAL == 0) {
                coroutineContext.ensureActive()
            }

            val current = queue.poll()!!
            interiorPixels.add(targetColor)

            val neighbors = listOf(
                Point(current.x, current.y - 1),
                Point(current.x + 1, current.y),
                Point(current.x, current.y + 1),
                Point(current.x - 1, current.y)
            )

            for (neighbor in neighbors) {
                if (neighbor.x in 0 until width && neighbor.y in 0 until height) {
                    val pixelIndex = neighbor.y * width + neighbor.x
                    if (!visited[pixelIndex] && localPixels[pixelIndex] == targetColor) {
                        visited[pixelIndex] = true
                        queue.add(neighbor)
                    }
                }
            }
        }
        return interiorPixels
    }

    private const val CANCELLATION_CHECK_INTERVAL = 1000
}