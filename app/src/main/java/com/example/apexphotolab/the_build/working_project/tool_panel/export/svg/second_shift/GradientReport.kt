package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift

import android.graphics.Point

/**
 * Job: Gradient Report.
 * Responsibility: Modeling a discovered gradient transition for handoff to the gradient generation pipeline.
 */
data class GradientReport(
    val scoutId: Int,
    val path: List<Point>,
    val startColor: Int,
    val endColor: Int,
    val isBorder: Boolean
)
