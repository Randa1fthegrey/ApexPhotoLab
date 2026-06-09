package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.scouts

/**
 * Job: Gradient Intelligence Agency.
 * Responsibility: Collecting and providing gradient reports discovered by scouts during a single shift.
 */
object GradientIntelligenceAgency {

    fun record(report: GradientReport) {
        reports.add(report)
    }

    fun getReportsForScout(scoutId: Int): List<GradientReport> {
        return reports.filter { it.scoutId == scoutId }
    }

    fun clear() {
        reports.clear()
    }

    private val reports = mutableListOf<GradientReport>()
}