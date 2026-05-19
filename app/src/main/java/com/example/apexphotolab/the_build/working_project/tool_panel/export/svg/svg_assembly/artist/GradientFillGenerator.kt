package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist

import android.graphics.Point

/**
 * Job: Gradient Fill Generator.
 * Responsibility: Taking a shape outline and gradient definition and producing the final SVG gradient and path elements.
 */
object GradientFillGenerator {

    data class GradientInfo(
        val id: String,
        val startColor: Int,
        val endColor: Int,
        val direction: GradientDirection
    )

    enum class GradientDirection {
        HORIZONTAL, VERTICAL, DIAGONAL
    }

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
                append("<path d=\"${openPathData.toString().trim()}\" fill=\"none\" stroke=\"url(#$gradientId)\" stroke-width=\"1.2\" stroke-linecap=\"round\" />")
            }
        }
    }
}