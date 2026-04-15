package com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift

import android.graphics.Point

/**
 * Data model for capturing discovered gradient transitions during tracing.
 * This "Intelligence Report" is handed off to the Gradient Smearing Team.
 */
data class GradientReport(
    val scoutId: Int,
    val path: List<Point>,
    val startColor: Int,
    val endColor: Int,
    val isBorder: Boolean
)

/**
 * A container for all discovered gradients in a single shift.
 */
object GradientIntelligenceAgency {
    private val reports = mutableListOf<GradientReport>()

    fun record(report: GradientReport) {
        reports.add(report)
    }

    fun getReportsForScout(scoutId: Int): List<GradientReport> {
        return reports.filter { it.scoutId == scoutId }
    }

    fun clear() {
        reports.clear()
    }
}
