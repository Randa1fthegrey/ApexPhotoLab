package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Bitmap
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.GradientIntelligenceAgency

/**
 * Job: Gradient Scout Orchestrator.
 * Responsibility: Coordinating all gradient scouts to pre-scan for gradient transitions
 * before any structural tracing begins.
 */
object GradientScoutOrchestrator {

    fun run(quantizedImage: Bitmap, originalImage: Bitmap) {
        GradientIntelligenceAgency.clear()
        scouts.forEach { scout ->
            val reports = scout.scout(quantizedImage, originalImage)
            reports.forEach { GradientIntelligenceAgency.record(it) }
        }
    }

    private val scouts = listOf(
        RedGradientScout, GreenGradientScout, BlueGradientScout,
        YellowGradientScout, CyanGradientScout, MagentaGradientScout,
        WhiteGradientScout, BlackGradientScout, GreyGradientScout,
        AlphaGradientScout
    )
}
