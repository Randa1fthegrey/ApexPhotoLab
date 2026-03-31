package com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist

import android.graphics.Bitmap
import android.graphics.Point
import java.util.UUID

/**
 * The "Universal Smear" Tool.
 * High-Fidelity Update: Orientation-Aware Sampling. 
 * Correctly identifies the start and end of a shape's span to apply gradients.
 */
object GradientPainter {

    fun paint(path: List<Point>, source: Bitmap): Pair<String, String> {
        if (path.size < 4) return Pair("", "")

        val points = path.filter { it.x != -1 }
        if (points.isEmpty()) return Pair("", "")

        // 1. Calculate the bounding box to determine orientation
        val minX = points.minOf { it.x }; val maxX = points.maxOf { it.x }
        val minY = points.minOf { it.y }; val maxY = points.maxOf { it.y }
        val w = maxX - minX
        val h = maxY - minY

        // 2. Identify start and end points based on the longest span
        val p1: Point
        val p2: Point
        if (w > h) {
            // Horizontal span (e.g., Top/Bottom border)
            p1 = Point(minX, (minY + maxY) / 2)
            p2 = Point(maxX, (minY + maxY) / 2)
        } else {
            // Vertical span (e.g., Left/Right border)
            p1 = Point((minX + maxX) / 2, minY)
            p2 = Point((minX + maxX) / 2, maxY)
        }

        // 3. SMART SAMPLING: Probe inward to avoid transparency leaks
        val startColor = sampleDeep(p1, source, 5)
        val endColor = sampleDeep(p2, source, 5)

        val startHex = String.format("#%06X", 0xFFFFFF and startColor)
        val endHex = String.format("#%06X", 0xFFFFFF and endColor)

        val gradId = "grad_${UUID.randomUUID().toString().take(8)}"

        val gradientDef = """
            <linearGradient id="$gradId" x1="${p1.x}" y1="${p1.y}" x2="${p2.x}" y2="${p2.y}" gradientUnits="userSpaceOnUse">
                <stop offset="0%" stop-color="$startHex" />
                <stop offset="100%" stop-color="$endHex" />
            </linearGradient>
        """.trimIndent()

        val d = PathDataGenerator.generate(path)
        val pathElement = "<path d=\"$d\" fill=\"url(#$gradId)\" stroke=\"url(#$gradId)\" stroke-width=\"0.5\" stroke-linejoin=\"round\" />"

        return Pair(gradientDef, pathElement)
    }

    private fun sampleDeep(p: Point, source: Bitmap, depth: Int): Int {
        val centerX = source.width / 2
        val centerY = source.height / 2
        var sx = p.x
        var sy = p.y
        if (sx < centerX) sx += depth else sx -= depth
        if (sy < centerY) sy += depth else sy -= depth
        return source.getPixel(sx.coerceIn(0, source.width - 1), sy.coerceIn(0, source.height - 1))
    }
}
