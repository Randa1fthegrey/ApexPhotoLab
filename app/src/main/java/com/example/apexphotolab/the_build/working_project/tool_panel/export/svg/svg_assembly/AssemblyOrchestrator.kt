package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.CensusReport
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.color_blending.ColorBlendingRoutingManager

/**
 * Job: Assembly Orchestrator.
 * Responsibility: Delegating path coloring work to the color blending routing manager.
 */
object AssemblyOrchestrator {

    suspend fun run(
        pathFragments: List<List<Point>>,
        censusReports: List<CensusReport>,
        quantizedImage: Bitmap,
        sourceImage: Bitmap
    ): List<String> {
        return ColorBlendingRoutingManager.route(pathFragments, censusReports, quantizedImage, sourceImage)
    }
}