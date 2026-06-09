package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.gradient

/**
 * Job: Gradient Vector Math.
 * Responsibility: Calculating SVG linear gradient coordinates (x1, y1, x2, y2) based on direction.
 */
object GradientVectorMath {

    data class GradientCoords(val x1: String, val y1: String, val x2: String, val y2: String)

    fun calculate(direction: GradientFillGenerator.GradientDirection): GradientCoords {
        return when (direction) {
            GradientFillGenerator.GradientDirection.HORIZONTAL -> GradientCoords("0%", "0%", "100%", "0%")
            GradientFillGenerator.GradientDirection.VERTICAL -> GradientCoords("0%", "0%", "0%", "100%")
            GradientFillGenerator.GradientDirection.DIAGONAL -> GradientCoords("0%", "0%", "100%", "100%")
        }
    }
}