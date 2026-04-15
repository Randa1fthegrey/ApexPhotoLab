package com.example.apexphotolab.working_project.tool_panel.export.svg.svg_assembly.artist

import android.graphics.Point

/**
 * File 7 (Artist Phase): The Gradient Fill Generator.
 * Takes a shape outline and a gradient definition, and generates the final
 * drawable SVG code, including the gradient definition and the path.
 * Updated: Separates open and closed paths to prevent diagonal "fill chords".
 */
object GradientFillGenerator {

    /**
     * Defines the properties of a gradient to be rendered.
     */
    data class GradientInfo(
        val id: String,
        val startColor: Int,
        val endColor: Int,
        val direction: GradientDirection
    )

    enum class GradientDirection {
        HORIZONTAL, VERTICAL, DIAGONAL
    }

    /**
     * Creates a filled SVG path element with an associated linear gradient.
     * @param paths The list of paths forming the shape's outline.
     * @param info The description of the gradient to apply.
     * @return A string containing the SVG <defs> for the gradient and the <path> elements.
     */
    fun generate(paths: List<List<Point>>, info: GradientInfo): String {
        val gradientDef = buildGradientDefinition(info)
        val pathElements = buildPathElements(paths, info.id, info.startColor)
        
        return "$gradientDef\n$pathElements"
    }

    private fun buildGradientDefinition(info: GradientInfo): String {
        val startHex = String.format("#%06X", 0xFFFFFF and info.startColor)
        val endHex = String.format("#%06X", 0xFFFFFF and info.endColor)

        val (x1, y1, x2, y2) = when (info.direction) {
            GradientDirection.HORIZONTAL -> Triple("0%", "0%", "100%").let { arrayOf(it.first, it.second, it.third, it.second) }
            GradientDirection.VERTICAL -> Triple("0%", "0%", "0%").let { arrayOf(it.first, it.second, it.third, "100%") }
            GradientDirection.DIAGONAL -> arrayOf("0%", "0%", "100%", "100%")
        }

        return """
    <linearGradient id="${info.id}" x1="$x1" y1="$y1" x2="$x2" y2="$y2">
      <stop offset="0%" style="stop-color:$startHex;stop-opacity:1" />
      <stop offset="100%" style="stop-color:$endHex;stop-opacity:1" />
    </linearGradient>"""
    }

    private fun buildPathElements(paths: List<List<Point>>, gradientId: String, colorInt: Int): String {
        val closedPathData = StringBuilder()
        val openPathData = StringBuilder()

        // 1. Determine which fragments are closed loops vs open lines.
        paths.forEach { path ->
            val (data, isClosed) = PathDataGenerator.generateWithStatus(path)
            if (isClosed) {
                closedPathData.append(data).append(" ")
            } else {
                openPathData.append(data).append(" ")
            }
        }

        // 2. Build the path tags.
        return buildString {
            // Closed loops get the full gradient fill.
            if (closedPathData.isNotEmpty()) {
                append("<path d=\"${closedPathData.toString().trim()}\" fill=\"url(#$gradientId)\" fill-rule=\"evenodd\" />")
            }

            // Open paths get fill="none" and a stroke to prevent the chord jump.
            if (openPathData.isNotEmpty()) {
                if (isNotEmpty()) append("\n")
                // Using the gradient for the stroke as well to maintain visual consistency.
                append("<path d=\"${openPathData.toString().trim()}\" fill=\"none\" stroke=\"url(#$gradientId)\" stroke-width=\"1.2\" stroke-linecap=\"round\" />")
            }
        }
    }

}
