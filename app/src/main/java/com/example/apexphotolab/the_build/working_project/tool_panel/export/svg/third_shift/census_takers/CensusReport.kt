package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers

/**
 * Job: Census Report.
 * Responsibility: Carrying the findings for a single shape from a Census Taker.
 */
data class CensusReport(
    val dominantColor: Int,
    val secondaryColor: Int,
    val totalPixels: Int,
    val blendRatio: Float,
    val complexityScore: Int
)