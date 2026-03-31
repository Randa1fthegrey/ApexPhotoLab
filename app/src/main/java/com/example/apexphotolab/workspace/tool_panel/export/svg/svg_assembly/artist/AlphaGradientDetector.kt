package com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import java.util.LinkedList

/**
 * A single-responsibility utility for detecting all transparent regions in an image.
 * Updated: Strict Alpha Shield. Prevents "leakage" into semi-transparent edges of solid shapes.
 */
object AlphaGradientDetector {

    data class AlphaGradientInfo(
        val blob: HashSet<Point>,
        val startAlpha: Int,
        val endAlpha: Int,
        val direction: GradientFillGenerator.GradientDirection,
        val width: Int,
        val height: Int
    )

    private const val STRICT_ALPHA_THRESHOLD = 50 // Only capture very low alpha (background)

    fun detect(image: Bitmap): List<AlphaGradientInfo> {
        val width = image.width
        val height = image.height
        val pixels = IntArray(width * height)
        image.getPixels(pixels, 0, width, 0, 0, width, height)

        val transparentBlobs = findTransparentBlobs(pixels, width, height)
        val reports = mutableListOf<AlphaGradientInfo>()

        for (blob in transparentBlobs) {
            val report = AlphaSlopeAnalyzer.analyze(blob, pixels, width, height)
            reports.add(report)
        }

        return reports
    }

    private fun findTransparentBlobs(pixels: IntArray, width: Int, height: Int): List<HashSet<Point>> {
        val blobs = mutableListOf<HashSet<Point>>()
        val visited = BooleanArray(pixels.size)
        val borderBuffer = 50 

        for (y in borderBuffer until height - borderBuffer) {
            for (x in borderBuffer until width - borderBuffer) {
                val index = y * width + x
                
                // 1. Initial trigger: Must be very transparent
                if (Color.alpha(pixels[index]) < STRICT_ALPHA_THRESHOLD && !visited[index]) {
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
                                // 2. Expansion trigger: Must ALSO be very transparent
                                // This prevents the crew from flowing into vibrant, semi-aliased shapes
                                if (Color.alpha(pixels[neighborIndex]) < STRICT_ALPHA_THRESHOLD && !visited[neighborIndex]) {
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
