package com.example.apexphotolab.workspace.toolbars.export.svg.svg_assembly.artist

import android.graphics.Color
import android.graphics.Point
import kotlin.math.abs

/**
 * A single-responsibility utility for analyzing the properties of a transparent blob.
 */
object AlphaSlopeAnalyzer {

    private data class BoundingBox(val left: Int, val top: Int, val right: Int, val bottom: Int)

    /**
     * Analyzes a blob of pixels to determine its dominant alpha gradient direction and intensity.
     */
    fun analyze(
        blob: HashSet<Point>,
        pixels: IntArray,
        imageWidth: Int,
        imageHeight: Int
    ): AlphaGradientDetector.AlphaGradientInfo {
        if (blob.isEmpty()) {
            return AlphaGradientDetector.AlphaGradientInfo(blob, 255, 255, GradientFillGenerator.GradientDirection.HORIZONTAL, imageWidth, imageHeight)
        }

        val boundingBox = blob.getBoundingBox()
        val leftEdgeAlpha = blob.filter { it.x == boundingBox.left }.map { Color.alpha(pixels[it.y * imageWidth + it.x]) }.average().toInt()
        val rightEdgeAlpha = blob.filter { it.x == boundingBox.right }.map { Color.alpha(pixels[it.y * imageWidth + it.x]) }.average().toInt()
        val topEdgeAlpha = blob.filter { it.y == boundingBox.top }.map { Color.alpha(pixels[it.y * imageWidth + it.x]) }.average().toInt()
        val bottomEdgeAlpha = blob.filter { it.y == boundingBox.bottom }.map { Color.alpha(pixels[it.y * imageWidth + it.x]) }.average().toInt()

        val horizontalDiff = abs(leftEdgeAlpha - rightEdgeAlpha)
        val verticalDiff = abs(topEdgeAlpha - bottomEdgeAlpha)

        return if (horizontalDiff > verticalDiff) {
            AlphaGradientDetector.AlphaGradientInfo(blob, leftEdgeAlpha, rightEdgeAlpha, GradientFillGenerator.GradientDirection.HORIZONTAL, imageWidth, imageHeight)
        } else {
            AlphaGradientDetector.AlphaGradientInfo(blob, topEdgeAlpha, bottomEdgeAlpha, GradientFillGenerator.GradientDirection.VERTICAL, imageWidth, imageHeight)
        }
    }

    private fun HashSet<Point>.getBoundingBox(): BoundingBox {
        val minX = this.minOfOrNull { it.x } ?: 0
        val maxX = this.maxOfOrNull { it.x } ?: 0
        val minY = this.minOfOrNull { it.y } ?: 0
        val maxY = this.maxOfOrNull { it.y } ?: 0
        return BoundingBox(minX, minY, maxX, maxY)
    }
}
