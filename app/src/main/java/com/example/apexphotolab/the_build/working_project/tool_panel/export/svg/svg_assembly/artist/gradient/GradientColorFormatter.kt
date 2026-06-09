package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.gradient

/**
 * Job: Gradient Color Formatter.
 * Responsibility: Converting ARGB color integers into standard SVG hex strings.
 */
object GradientColorFormatter {

    fun toHex(color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }
}