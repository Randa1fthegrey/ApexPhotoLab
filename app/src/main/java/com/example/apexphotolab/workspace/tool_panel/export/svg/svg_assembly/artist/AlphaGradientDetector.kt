package com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import java.util.LinkedList

/**
 * A single-responsibility utility for detecting all transparent regions in an image.
 */
object AlphaGradientDetector {

    /**
     * Defines the results of an alpha gradient analysis.
     * Now includes width and height for edge detection.
     */
    data class AlphaGradientInfo(
        val blob: HashSet<Point>,
        val startAlpha: Int,
        val endAlpha: Int,
        val direction: GradientFillGenerator.GradientDirection,
        val width: Int,
        val height: Int
    )

    /**
     * Finds and analyzes all transparent regions in the image.
     */
    fun detect(image: Bitmap): List<AlphaGradientInfo> {
        val width = image.width
        val height = image.height
        val pixels = IntArray(width * height)
        image.getPixels(pixels, 0, width, 0, 0, width, height)

        val transparentBlobs = findTransparentBlobs(pixels, width, height)
        val reports = mutableListOf<AlphaGradientInfo>()

        for (blob in transparentBlobs) {
            // Delegate the analysis job to the single-responsibility analyzer.
            // Pass the image height as well to match the new signature.
            val report = AlphaSlopeAnalyzer.analyze(blob, pixels, width, height)
            reports.add(report)
        }

        return reports
    }

    /**
     * Scans the image to find all contiguous blobs of semi-transparent pixels.
     */
    private fun findTransparentBlobs(pixels: IntArray, width: Int, height: Int): List<HashSet<Point>> {
        val blobs = mutableListOf<HashSet<Point>>()
        val visited = BooleanArray(pixels.size)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x
                if (Color.alpha(pixels[index]) < 255 && !visited[index]) {
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
                                if (Color.alpha(pixels[neighborIndex]) < 255 && !visited[neighborIndex]) {
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
