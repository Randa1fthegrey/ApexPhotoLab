package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils

import android.graphics.Bitmap
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.AlphaFillGenerator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.AlphaGradientDetector

/**
 * Job: Transparency Crew Orchestrator.
 * Responsibility: Running the full transparency detection and SVG generation pipeline.
 */
object TransparencyCrewOrchestrator {

    fun run(quantizedImage: Bitmap): List<String> {
        val alphaReports = AlphaGradientDetector.detect(quantizedImage)
        val result = AlphaFillGenerator.generate(alphaReports)
        
        return if (result.isNotEmpty()) {
            listOf("<!-- === ALPHA BLOBS === -->") + result
        } else {
            emptyList()
        }
    }
}
