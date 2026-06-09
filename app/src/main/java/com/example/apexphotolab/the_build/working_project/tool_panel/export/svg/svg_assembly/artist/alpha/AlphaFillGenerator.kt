package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.alpha

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.AlphaGradientDetector

/**
 * Job: Alpha Fill Generator (Orchestrator).
 * Responsibility: Coordinating VRAM preparation, coordinate scanning, path tracing, and SVG painting for alpha regions.
 */
object AlphaFillGenerator {

    fun generate(alphaInfos: List<AlphaGradientDetector.AlphaGradientInfo>): List<String> {
        val svgSnippets = mutableListOf<String>()
        var gradientIdCounter = 0

        alphaInfos.forEach { info ->
            // 1. PREPARATION
            val vram = AlphaVramPreparer.prepare(info)

            // 2. SCANNING
            val edges = AlphaCoordinateScanner.scan(vram, info)

            // 3. TRACING
            val paths = AlphaPathTracer.trace(edges, vram, info)

            // 4. PAINTING
            if (paths.isNotEmpty()) {
                val gradientId = "alphaGrad${gradientIdCounter++}"
                svgSnippets.add(AlphaSvgPainter.paint(paths, info, gradientId))
            }
        }

        return svgSnippets
    }
}