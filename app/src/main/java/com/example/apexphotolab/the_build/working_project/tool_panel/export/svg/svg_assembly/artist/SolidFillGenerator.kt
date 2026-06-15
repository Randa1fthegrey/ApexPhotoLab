package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist

import android.graphics.Color
import android.graphics.Point

/**
 * Job: Solid Fill Generator.
 * Responsibility: Generating SVG path elements with solid color fills for closed and open shape fragments.
 */
object SolidFillGenerator {

    fun generate(paths: List<List<Point>>, color: Int): String {
        if (paths.isEmpty() || paths.first().isEmpty()) return ""

        val alpha = Color.alpha(color)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val colorHex = String.format("#%06X", 0xFFFFFF and color)

        val opacity = alpha / 255.0
        val fillAttr = colorHex

        // Identification: Is this a Fill (White) or a Structural Outline (Grey/Black)?
        val isWhiteFill = r > val_util.COLOR_WHITE_THRESHOLD && g > val_util.COLOR_WHITE_THRESHOLD && b > val_util.COLOR_WHITE_THRESHOLD
        val isStructuralOutline = (r == g && g == b && r < val_util.COLOR_STRUCTURAL_THRESHOLD)

        val strokeWidth = when {
            isWhiteFill -> val_util.STROKE_WHITE
            isStructuralOutline -> val_util.STROKE_STRUCTURAL
            else -> val_util.STROKE_DEFAULT
        }

        val closedPathData = StringBuilder()
        val openPathData = StringBuilder()

        paths.forEach { path ->
            val (data, isClosed) = PathDataGenerator.generateWithStatus(path)
            if (isClosed) {
                closedPathData.append(data).append(" ")
            } else {
                openPathData.append(data).append(" ")
            }
        }

        return buildString {
            if (closedPathData.isNotEmpty()) {
                append("<path d=\"${closedPathData.toString().trim()}\" fill=\"$fillAttr\" fill-opacity=\"$opacity\" stroke=\"$colorHex\" stroke-opacity=\"$opacity\" stroke-width=\"$strokeWidth\" stroke-linejoin=\"round\" stroke-linecap=\"round\" fill-rule=\"evenodd\" />")
            }
            if (openPathData.isNotEmpty()) {
                if (isNotEmpty()) append("\n")
                // FORCE FILL ON OPEN PATHS: Bars touching edges are often "open" but represent solid regions
                append("<path d=\"${openPathData.toString().trim()} Z\" fill=\"$fillAttr\" fill-opacity=\"$opacity\" stroke=\"$colorHex\" stroke-opacity=\"$opacity\" stroke-width=\"$strokeWidth\" stroke-linejoin=\"round\" stroke-linecap=\"round\" fill-rule=\"evenodd\" />")
            }
        }
    }
}
