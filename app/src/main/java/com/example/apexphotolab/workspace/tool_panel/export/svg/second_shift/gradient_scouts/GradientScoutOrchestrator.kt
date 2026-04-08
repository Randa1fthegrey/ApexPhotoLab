package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Bitmap
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.GradientIntelligenceAgency

/**
 * The Leader of Team 0 (Gradient Scouts).
 * Coordinates the pre-scan for gradients before any tracing begins.
 */
object GradientScoutOrchestrator {

    private val scouts = listOf(
        RedGradientScout, GreenGradientScout, BlueGradientScout,
        YellowGradientScout, CyanGradientScout, MagentaGradientScout,
        WhiteGradientScout, BlackGradientScout, GreyGradientScout,
        AlphaGradientScout
    )

    /**
     * Runs all scouts to populate the GradientIntelligenceAgency.
     * Scans both images to find the "True North" of color gradients.
     */
    fun run(quantizedImage: Bitmap, originalImage: Bitmap) {
        GradientIntelligenceAgency.clear()
        scouts.forEach { scout ->
            val reports = scout.scout(quantizedImage, originalImage)
            reports.forEach { GradientIntelligenceAgency.record(it) }
        }
    }
}
