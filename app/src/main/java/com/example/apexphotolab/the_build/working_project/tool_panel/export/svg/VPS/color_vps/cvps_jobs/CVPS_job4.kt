package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point

/**
 * Job: CVPS Job 4 - Sanitizers.
 * Responsibility: Filtering out path artifacts from a color group after tracing.
 */
object CVPS_job4 {

    data class SanitizerData(
        val paths: List<List<Point>>,
        var result: List<List<Point>> = emptyList()
    )

    fun execute(colorId: Int, data: Any?) {
        val sData = data as? SanitizerData ?: return

        // Currently all sanitizers use the same rule: filter paths with size > 2
        sData.result = sData.paths.filter { it.size > 2 }
    }
}