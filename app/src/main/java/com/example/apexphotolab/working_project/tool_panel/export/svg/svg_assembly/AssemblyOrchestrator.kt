package com.example.apexphotolab.working_project.tool_panel.export.svg.svg_assembly

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift.CensusReport
import com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift.color_blending.ColorBlendingRoutingManager

/**
 * The main orchestrator for the Assembly Shift.
 * Updated: Delegates routing to the ColorBlendingRoutingManager in Third Shift.
 */
object AssemblyOrchestrator {

    suspend fun run(
        pathFragments: List<List<Point>>,
        censusReports: List<CensusReport>,
        sourceImage: Bitmap
    ): List<String> {
        return ColorBlendingRoutingManager.route(pathFragments, censusReports, sourceImage)
    }
}
