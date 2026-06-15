package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.blending

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.CVPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job6
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.CVPS_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusReport

/**
 * Job: Routing manager for the color blending desk (Orchestrator).
 * Responsibility: Coordinating fragment grouping and routing them to family-specific CVPS blending workers.
 */
object ColorBlendingRoutingManager {

    suspend fun route(
        pathFragments: List<List<Point>>,
        censusReports: List<CensusReport>,
        quantizedImage: Bitmap,
        sourceImage: Bitmap
    ): List<String> {

        // 1. GROUPING
        val familyGroups = ColorBlendingFragmentGrouper.group(pathFragments, censusReports)

        val allSvgPaths = mutableListOf<String>()

        // 2. ROUTING
        for ((groupIndex, indices) in familyGroups) {
            val familyPaths = indices.map { pathFragments[it] }
            val familyReports = indices.map { censusReports[it] }

            val worker = CVPS_HiringDepartment.getWorkerByColorId(groupIndex)
            CVPS_Audit.logCompute(6, groupIndex)

            val data = CVPS_job6.BlendingData(familyPaths, familyReports, quantizedImage, sourceImage)
            worker.runColorTask(6, data)
            val result = data.result

            if (result.isNotEmpty()) {
                val label = ColorBlendingBucketLabeller.getLabel(groupIndex)
                allSvgPaths.add("<!-- === $label === -->")
                allSvgPaths.addAll(result)
            }
        }

        return allSvgPaths
    }
}