package com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist

import android.graphics.Color
import android.graphics.Point

/**
 * File 6 (Artist Phase): The Solid Shape Fill Generator.
 * Takes a complete shape outline and a solid color, and generates the final
 * drawable SVG <path> element.
 * Updated: Separates open and closed paths to prevent diagonal "fill chords".
 */
object SolidFillGenerator {

    /**
     * Creates SVG path elements from one or more path outlines.
     * @param paths A list containing the ordered points for the shape's paths.
     * @param color The ARGB integer color for the fill and stroke.
     * @return The complete SVG snippet containing one or more <path> elements.
     */
    fun generate(paths: List<List<Point>>, color: Int): String {
        if (paths.isEmpty() || paths.first().isEmpty()) return ""

        val alpha = Color.alpha(color)
        val colorHex = String.format("#%06X", 0xFFFFFF and color)
        val opacity = alpha / 255.0

        val closedPathData = StringBuilder()
        val openPathData = StringBuilder()

        // 1. Sort sub-paths by their closure status.
        paths.forEach { path ->
            val (data, isClosed) = PathDataGenerator.generateWithStatus(path)
            if (isClosed) {
                closedPathData.append(data).append(" ")
            } else {
                openPathData.append(data).append(" ")
            }
        }

        // 2. Build the SVG snippet.
        return buildString {
            // Closed loops get both FILL and STROKE.
            if (closedPathData.isNotEmpty()) {
                append("<path d=\"${closedPathData.toString().trim()}\" fill=\"$colorHex\" fill-opacity=\"$opacity\" stroke=\"$colorHex\" stroke-opacity=\"$opacity\" stroke-width=\"1.2\" stroke-linejoin=\"round\" stroke-linecap=\"round\" fill-rule=\"nonzero\" />")
            }

            // Open lines get STROKE only (fill="none"). This kills the diagonal chords!
            if (openPathData.isNotEmpty()) {
                if (isNotEmpty()) append("\n")
                append("<path d=\"${openPathData.toString().trim()}\" fill=\"none\" stroke=\"$colorHex\" stroke-opacity=\"$opacity\" stroke-width=\"1.2\" stroke-linejoin=\"round\" stroke-linecap=\"round\" />")
            }
        }
    }
}
