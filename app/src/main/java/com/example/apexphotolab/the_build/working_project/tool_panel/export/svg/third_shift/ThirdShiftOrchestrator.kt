package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.CVPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job5
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.CVPS_Audit
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

        // 1. RESOLUTION: Determine the statistical CensusReport for every single fragment.
        val reports = ColoringDispatcher.resolveColorsInParallel(pathFragments, quantizedImage)

        // 2. GROUPING
        val familyGroups = ColorBlendingFragmentGrouper.group(pathFragments, reports)

        val allConsolidatedPaths = mutableListOf<List<Point>>()
        val allConsolidatedReports = mutableListOf<CensusReport>()

        // 3. CONSOLIDATION
        familyGroups.forEach { (groupIndex, indices) ->
            val familyPaths = indices.map { pathFragments[it] }
            val familyReports = indices.map { reports[it] }

            val worker = CVPS_HiringDepartment.getWorkerByColorId(groupIndex)
            CVPS_Audit.logCompute(5, groupIndex)

            val data = CVPS_job5.ConsolidationData(familyPaths, familyReports)
            worker.runColorTask(5, data)

            val (resultPaths, resultReports) = data.result ?: Pair(familyPaths, familyReports)

            allConsolidatedPaths.addAll(resultPaths)
            allConsolidatedReports.addAll(resultReports)
        }

        return Pair(allConsolidatedPaths, allConsolidatedReports)
    }
}
