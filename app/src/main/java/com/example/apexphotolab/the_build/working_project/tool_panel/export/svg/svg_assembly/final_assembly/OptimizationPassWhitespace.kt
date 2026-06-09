package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.final_assembly

/**
 * Job: Optimization Pass - Whitespace.
 * Responsibility: Removing redundant whitespace and newlines from an SVG string to minimize file size.
 */
object OptimizationPassWhitespace {

    fun execute(svg: String): String {
        return svg.replace(Regex("\\s*\\n\\s*|\\s{2,}"), " ").trim()
    }
}
