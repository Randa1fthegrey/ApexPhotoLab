package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.CVPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job3_Solidify
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.CVPS_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.SVG_Unified_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram.SecondShiftVramManager
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure.SecondShiftPixelExtractor
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.blending.ColorBlendingFragmentGrouper
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusReport
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.dispatchers.ColoringDispatcher

/**
 * Job: Third Shift Orchestrator (The Master Manager).
 * Responsibility: Coordinating resolution, grouping, and consolidation to produce purified geometric elements.
 */
object ThirdShiftOrchestrator {

    suspend fun run(
        pathFragments: List<List<Point>>,
        quantizedImage: Bitmap,
        sourceImage: Bitmap
    ): Pair<List<List<Point>>, List<CensusReport>> {

        val width = quantizedImage.width
        val height = quantizedImage.height
        val pixels = SecondShiftPixelExtractor.extract(quantizedImage)
        val vram = SecondShiftVramManager.getMasterVram()

        // 1. RESOLUTION (ISOLATION MODE): Trust the bucket origin.
        // Instead of re-calculating the color statistically, we assign GREY (ID 9) 
        // to every fragment collected from the GREY bucket.
        val reports = pathFragments.map { 
            CensusReport(9, 0xFF808080.toInt(), 0, 0f, 0) 
        }

        // 2. GROUPING
        val familyGroups = ColorBlendingFragmentGrouper.group(pathFragments, reports)

        val allConsolidatedPaths = mutableListOf<List<Point>>()
        val allConsolidatedReports = mutableListOf<CensusReport>()

        // 3. CONSOLIDATION & SOLIDIFICATION (CVPS Job 3)
        SVG_Unified_Audit.logHandoff("ThirdShiftOrchestrator", "CVPS_job3_Solidify", "Weld & Stitch")
        for ((groupIndex, indices) in familyGroups) {
            val familyPaths = indices.map { pathFragments[it] }
            val familyReports = indices.map { reports[it] }

            val worker = CVPS_HiringDepartment.getWorkerByColorId(groupIndex)
            CVPS_Audit.logCompute(3, groupIndex)

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
