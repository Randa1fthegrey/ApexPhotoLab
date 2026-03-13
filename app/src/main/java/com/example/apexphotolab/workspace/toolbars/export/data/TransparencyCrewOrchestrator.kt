package com.example.apexphotolab.workspace.toolbars.export.data

import android.graphics.Bitmap
import com.example.apexphotolab.workspace.toolbars.export.svg.svg_assembly.artist.AlphaFillGenerator
import com.example.apexphotolab.workspace.toolbars.export.svg.svg_assembly.artist.AlphaGradientDetector

/**
 * A single-responsibility orchestrator for the Transparency Crew.
 * This crew runs in parallel to the main Solid Color Crew.
 */
object TransparencyCrewOrchestrator {

    /**
     * Takes a quantized image and runs the entire transparency detection and generation pipeline.
     * @param quantizedImage The color-quantized image from the First Shift.
     * @return A list of strings, each being a complete SVG snippet for a transparent region.
     */
    fun run(quantizedImage: Bitmap): List<String> {
        val alphaReports = AlphaGradientDetector.detect(quantizedImage)
        return AlphaFillGenerator.generate(alphaReports)
    }
}