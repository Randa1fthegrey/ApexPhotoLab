package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools

import android.graphics.Bitmap
import android.graphics.Point
import android.util.Log

/**
 * Diagnostic tool to perform a pixel-perfect audit of the SVG results against the source image.
 * This gives the AI "eyes" to see where the shapes and colors are drifting.
 */
object FidelityAuditLogger {
    private const val TAG = "SVG"

    /**
     * Compares the final SVG against the source Bitmap.
     */
    fun runAudit(source: Bitmap, svg: String) {
        Log.d(TAG, "[AUDIT] Starting Fidelity Audit...")

        val pathRegex = "<path d=\"([^\"]*)\" fill=\"([^\"]*)\"".toRegex()
        val pathMatches = pathRegex.findAll(svg).toList()

        if (pathMatches.isEmpty()) {
            Log.w(TAG, "[AUDIT] Failed: No paths found in SVG string.")
            return
        }

        pathMatches.forEachIndexed { index, match ->
            val pathData = match.groupValues[1]
            val svgFill = match.groupValues[2]

            // 1. Calculate Bounding Box and Center Point from path data
            val points = extractPoints(pathData)
            if (points.isEmpty()) return@forEachIndexed

            val minX = points.minOf { it.x }
            val maxX = points.maxOf { it.x }
            val minY = points.minOf { it.y }
            val maxY = points.maxOf { it.y }

            val centerX = (minX + maxX) / 2
            val centerY = (minY + maxY) / 2

            // 2. Sample the source image at the center point
            if (centerX in 0 until source.width && centerY in 0 until source.height) {
                val sourcePixel = source.getPixel(centerX, centerY)
                val sourceHex = String.format("#%06X", 0xFFFFFF and sourcePixel)

                // 3. Compare SVG color vs Source color
                if (svgFill.startsWith("#")) {
                    val normalizedSvgFill = svgFill.uppercase()
                    val normalizedSourceHex = sourceHex.uppercase()

                    if (normalizedSvgFill == normalizedSourceHex) {
                        Log.d(TAG, "[AUDIT] Path #$index at ($centerX, $centerY): MATCH ($normalizedSvgFill)")
                    } else {
                        Log.e(TAG, "[AUDIT] Path #$index at ($centerX, $centerY): MISMATCH! SVG=$normalizedSvgFill, Source=$normalizedSourceHex")
                    }
                } else if (svgFill.startsWith("url")) {
                    Log.d(TAG, "[AUDIT] Path #$index at ($centerX, $centerY): Using Gradient/Alpha ($svgFill). Source was $sourceHex")
                }
            }
        }

        Log.d(TAG, "[AUDIT] Audit Complete.")
    }

    private fun extractPoints(pathData: String): List<Point> {
        val points = mutableListOf<Point>()
        // Simple parser for "M x y L x y" format
        val coordRegex = "([0-9]+) ([0-9]+)".toRegex()
        coordRegex.findAll(pathData).forEach { match ->
            val x = match.groupValues[1].toInt()
            val y = match.groupValues[2].toInt()
            points.add(Point(x, y))
        }
        return points
    }
}
