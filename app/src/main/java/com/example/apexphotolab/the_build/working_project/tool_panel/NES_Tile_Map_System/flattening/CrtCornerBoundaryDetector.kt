package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.flattening

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF

/**
 * Job: Bevel & Corner Boundary Detector.
 * Responsibility: Scans inward from image borders to locate the 4 active CRT screen corners:
 * - Top-Left Corner
 * - Top-Right Corner
 * - Bottom-Left Corner
 * - Bottom-Right Corner
 */
object CrtCornerBoundaryDetector {

    data class CrtCorners(
        val topLeft: PointF,
        val topRight: PointF,
        val bottomLeft: PointF,
        val bottomRight: PointF
    )

    fun detectCorners(bitmap: Bitmap, brightnessThreshold: Float = 0.08f): CrtCorners {
        val width = bitmap.width
        val height = bitmap.height

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // 1. Top-Left Corner Scan (Inward along diagonal)
        val topLeft = scanDiagonal(pixels, width, height, startX = 0, startY = 0, stepX = 1, stepY = 1, threshold = brightnessThreshold)
            ?: PointF(0f, 0f)

        // 2. Top-Right Corner Scan
        val topRight = scanDiagonal(pixels, width, height, startX = width - 1, startY = 0, stepX = -1, stepY = 1, threshold = brightnessThreshold)
            ?: PointF(width.toFloat(), 0f)

        // 3. Bottom-Left Corner Scan
        val bottomLeft = scanDiagonal(pixels, width, height, startX = 0, startY = height - 1, stepX = 1, stepY = -1, threshold = brightnessThreshold)
            ?: PointF(0f, height.toFloat())

        // 4. Bottom-Right Corner Scan
        val bottomRight = scanDiagonal(pixels, width, height, startX = width - 1, startY = height - 1, stepX = -1, stepY = -1, threshold = brightnessThreshold)
            ?: PointF(width.toFloat(), height.toFloat())

        return CrtCorners(topLeft, topRight, bottomLeft, bottomRight)
    }

    private fun scanDiagonal(
        pixels: IntArray,
        width: Int,
        height: Int,
        startX: Int,
        startY: Int,
        stepX: Int,
        stepY: Int,
        threshold: Float
    ): PointF? {
        val x = startX
        val y = startY
        val maxSteps = minOf(width / 2, height / 2)

        for (step in 0 until maxSteps) {
            val currX = (x + step * stepX).coerceIn(0, width - 1)
            val currY = (y + step * stepY).coerceIn(0, height - 1)
            val pixel = pixels[currY * width + currX]

            if (getPixelBrightness(pixel) > threshold) {
                return PointF(currX.toFloat(), currY.toFloat())
            }
        }
        return null
    }

    private fun getPixelBrightness(pixel: Int): Float {
        val r = Color.red(pixel) / 255f
        val g = Color.green(pixel) / 255f
        val b = Color.blue(pixel) / 255f
        return 0.2126f * r + 0.7152f * g + 0.0722f * b
    }
}
