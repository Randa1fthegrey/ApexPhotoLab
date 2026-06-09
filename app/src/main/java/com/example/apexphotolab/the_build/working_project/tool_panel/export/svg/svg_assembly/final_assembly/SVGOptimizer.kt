package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.final_assembly

/**
 * Job: SVG Optimizer (Orchestrator).
 * Responsibility: Coordinating multiple optimization passes to reduce SVG file size and complexity.
 */
object SVGOptimizer {

    fun optimize(svgContent: String): String {
        var result = svgContent

        // Pass 1: Whitespace removal
        result = OptimizationPassWhitespace.execute(result)

        // Pass 2: Gradient deduplication
        result = OptimizationPassGradients.execute(result)

        // Pass 3: Path simplification
        result = OptimizationPassPaths.execute(result)

        return result
    }
}
