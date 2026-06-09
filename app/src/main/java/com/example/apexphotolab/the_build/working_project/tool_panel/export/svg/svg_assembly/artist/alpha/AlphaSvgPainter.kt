package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.alpha

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.AlphaGradientDetector
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.gradient.GradientFillGenerator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.PathDataGenerator

/**
 * Job: Alpha SVG Painter.
 * Responsibility: Building SVG XML strings (linearGradients and paths) for alpha regions.
 */
object AlphaSvgPainter {

    fun paint(paths: List<List<Point>>, info: AlphaGradientDetector.AlphaGradientInfo, gradientId: String): String {
        val gradientDef = buildGradientDefinition(info, gradientId)
        val pathElements = buildPathElements(paths, gradientId)
        return "$gradientDef\n$pathElements"
    }

    private fun buildGradientDefinition(info: AlphaGradientDetector.AlphaGradientInfo, gradientId: String): String {
        val startOpacity = info.startAlpha / 255.0
        val endOpacity = info.endAlpha / 255.0

        val (x1, y1, x2, y2) = when (info.direction) {
            GradientFillGenerator.GradientDirection.HORIZONTAL -> arrayOf("0%", "0%", "100%", "0%")
            GradientFillGenerator.GradientDirection.VERTICAL -> arrayOf("0%", "0%", "0%", "100%")
            else -> arrayOf("0%", "0%", "100%", "100%")
        }

        return """
    <linearGradient id="$gradientId" x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}">
      <stop offset="0%" style="stop-color:black;stop-opacity:$startOpacity" />
      <stop offset="100%" style="stop-color:black;stop-opacity:$endOpacity" />
    </linearGradient>"""
    }

    private fun buildPathElements(paths: List<List<Point>>, gradientId: String): String {
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
                append("<path d=\"${closedPathData.toString().trim()}\" fill=\"url(#$gradientId)\" fill-rule=\"evenodd\" />")
            }
            if (openPathData.isNotEmpty()) {
                if (isNotEmpty()) append("\n")
                append("<path d=\"${openPathData.toString().trim()}\" fill=\"none\" stroke=\"url(#$gradientId)\" stroke-width=\"1.2\" stroke-linecap=\"round\" />")
            }
        }
    }
}