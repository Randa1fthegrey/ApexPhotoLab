package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.orchestration

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.CVPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job3_Solidify
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure.SecondShiftVramManager
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure.SecondShiftPixelExtractor
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.blending.ColorBlendingFragmentGrouper
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census.CensusReport
import kotlin.collections.iterator

/**
 * Job: Third Shift Orchestrator (The Master Manager).
 * Responsibility: Coordinating resolution, grouping, and consolidation to produce purified geometric elements.
 */
object ThirdShiftOrchestrator {

    suspend fun run(
        pathFragments: List<List<Point>>,
        inputReports: List<CensusReport>,
        quantizedImage: Bitmap,
        sourceImage: Bitmap
    ): Pair<List<List<Point>>, List<CensusReport>> {

        val width = quantizedImage.width
        val height = quantizedImage.height
        val pixels = SecondShiftPixelExtractor.extract(quantizedImage)
        val vram = SecondShiftVramManager.prepareMasterVram()

        // 1. RESOLUTION: Use origin color reports from Second Shift if available
        val reports: List<CensusReport> = if (inputReports.size == pathFragments.size) {
            inputReports
        } else if (pathFragments.isNotEmpty()) {
            ColoringDispatcher.resolveColorsInParallel(pathFragments, quantizedImage)
        } else {
            emptyList()
        }

        // 2. GROUPING
        val familyGroups = ColorBlendingFragmentGrouper.group(pathFragments, reports)

        val allConsolidatedPaths = mutableListOf<List<Point>>()
        val allConsolidatedReports = mutableListOf<CensusReport>()

        // 3. CONSOLIDATION & SOLIDIFICATION (CVPS Job 3)
        for ((groupIndex, indices) in familyGroups) {
            val familyPaths = indices.map { pathFragments[it] }
            val familyReports = indices.map { reports[it] }

            val worker = CVPS_HiringDepartment.getWorkerByColorId(groupIndex)

            val data = CVPS_job3_Solidify.SolidifyData(
                fragments = familyPaths,
                reports = familyReports,
                vram = vram,
                width = width,
                height = height,
                pixels = pixels
            )
            worker.runColorTask(3, data)

            val (resultPaths, resultReports) = data.result ?: Pair(familyPaths, familyReports)

            allConsolidatedPaths.addAll(resultPaths)
            allConsolidatedReports.addAll(resultReports)
        }

        return Pair(allConsolidatedPaths, allConsolidatedReports)
    }
}
