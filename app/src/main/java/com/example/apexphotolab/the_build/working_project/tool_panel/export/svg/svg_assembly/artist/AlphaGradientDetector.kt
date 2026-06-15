package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.alpha.AlphaPixelExtractor
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.alpha.AlphaSlopeAnalyzer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.gradient.GradientFillGenerator
import java.util.LinkedList

/**
 * Job: Alpha Gradient Detector (The Scanner).
 * Responsibility: Discovering connected transparent regions (blobs) via flood-fill analysis.
 */
object AlphaGradientDetector {

    data class AlphaGradientInfo(
        val blob: HashSet<Point>,
        val startAlpha: Int,
        val endAlpha: Int,
        val direction: GradientFillGenerator.GradientDirection,
        val width: Int,
        val height: Int,
        val pixels: IntArray
    )

    fun detect(image: Bitmap): List<AlphaGradientInfo> {
        val pixels = AlphaPixelExtractor.extract(image)
        val width = image.width
        val height = image.height

        val transparentBlobs = findTransparentBlobs(pixels, width, height)
        val reports = mutableListOf<AlphaGradientInfo>()

        for (blob in transparentBlobs) {
            reports.add(AlphaSlopeAnalyzer.analyze(blob, pixels, width, height))
        }

        return reports
    }

    private fun findTransparentBlobs(pixels: IntArray, width: Int, height: Int): List<HashSet<Point>> {
        val blobs = mutableListOf<HashSet<Point>>()
        val visited = BooleanArray(pixels.size)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x

                if (Color.alpha(pixels[index]) < val_util.ALPHA_THRESHOLD && !visited[index]) {
                    val newBlob = HashSet<Point>()
                    val queue = LinkedList<Point>()

                    queue.add(Point(x, y))
                    visited[index] = true

                    while (queue.isNotEmpty()) {
                        val current = queue.removeAt(0)
                        newBlob.add(current)

                        val neighbors = listOf(
                            Point(current.x, current.y - 1), Point(current.x + 1, current.y),
                            Point(current.x, current.y + 1), Point(current.x - 1, current.y)
                        )

                        for (neighbor in neighbors) {
                            if (neighbor.x in 0 until width && neighbor.y in 0 until height) {
                                val neighborIndex = neighbor.y * width + neighbor.x
                                if (Color.alpha(pixels[neighborIndex]) < val_util.ALPHA_THRESHOLD && !visited[neighborIndex]) {
                                    visited[neighborIndex] = true
                                    queue.add(neighbor)
                                }
                            }
                        }
                    }
                    blobs.add(newBlob)
                }
            }
        }
        return blobs
    }
}
