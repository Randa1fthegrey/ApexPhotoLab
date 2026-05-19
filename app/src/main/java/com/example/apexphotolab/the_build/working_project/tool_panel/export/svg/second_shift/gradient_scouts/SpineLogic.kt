package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.ColorGroupSorter
import kotlin.math.abs

/**
 * Job: The Surveyor.
 * Responsibility: Identifying the "Intensity Ridge" (the spine) of a color group,
 * constrained to the quantized shape to ensure no stray paths or cross-connections.
 */
object SpineLogic {

    /**
     * Finds the peak intensity path (the spine) within a specific color shape.
     */
    fun findSpine(quantizedImage: Bitmap, originalImage: Bitmap, groupIndex: Int): List<List<Point>> {
        val width = quantizedImage.width
        val height = quantizedImage.height

        val qPixels = IntArray(width * height)
        val oPixels = IntArray(width * height)

        quantizedImage.getPixels(qPixels, 0, width, 0, 0, width, height)
        originalImage.getPixels(oPixels, 0, width, 0, 0, width, height)

        // 1. Calculate Scores only for pixels belonging to the target group
        val scores = FloatArray(width * height) { -1f }
        val targetPoints = mutableListOf<Int>()

        for (i in qPixels.indices) {
            val qPixel = qPixels[i]
            // Only look within the SHAPE defined by the quantizer
            if (ColorGroupSorter.getGroupIndexForPixel(qPixel) == groupIndex) {
                scores[i] = calculateIntensityScore(oPixels[i], groupIndex)
                targetPoints.add(i)
            }
        }

        if (targetPoints.isEmpty()) return emptyList()

        // 2. Walk the Ridge
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

    /**
     * Calculates how "pure" a pixel is for its group.
     * Peak vibrancy = Peak score.
     */
    private fun calculateIntensityScore(pixel: Int, groupIndex: Int): Float {
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)

        val hsv = FloatArray(3)
        Color.RGBToHSV(r, g, b, hsv)
        val hue = hsv[0]
        val sat = hsv[1]
        val val_ = hsv[2]

        return when (groupIndex) {
            0 -> 1.0f - hueDistance(hue, 0f)      // Red
            3 -> 1.0f - hueDistance(hue, 45f)     // Yellow
            1 -> 1.0f - hueDistance(hue, 120f)    // Green
            4 -> 1.0f - hueDistance(hue, 180f)    // Cyan
            2 -> 1.0f - hueDistance(hue, 240f)    // Blue
            5 -> 1.0f - hueDistance(hue, 300f)    // Magenta
            6 -> val_ * (1.0f - sat)              // White (Bright, no color)
            7 -> 1.0f - (Color.alpha(pixel) / 255.0f) // Alpha (Transparent)
            8 -> 1.0f - val_                      // Black (Dark)
            9 -> (1.0f - sat) * (0.5f - abs(val_ - 0.5f)) // Grey (Neutral middle)
            else -> 0f
        }
    }

    private fun hueDistance(h1: Float, h2: Float): Float {
        val diff = abs(h1 - h2)
        val normalized = if (diff > 180) 360 - diff else diff
        return normalized / 180f // Returns 0.0 (exact match) to 1.0 (opposite)
    }
}
