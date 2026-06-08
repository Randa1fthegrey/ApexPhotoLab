package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.CVPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job6
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.CVPS_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.ColorBucketDiagnostic
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.EfficiencyAudit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.ColorGroupSorter

/**
 * Job: Routing manager for the color blending desk.
 * Responsibility: Receiving path shapes and census reports, then routing them to family-specific CVPS blending workers.
 */
object ColorBlendingRoutingManager {

    suspend fun route(
        pathFragments: List<List<Point>>,
        censusReports: List<CensusReport>,
        quantizedImage: Bitmap,
        sourceImage: Bitmap
    ): List<String> {

        // Group indices by their color family using the statistical dominant color
        val familyGroups = pathFragments.indices.groupBy { index ->
            ColorGroupSorter.getGroupIndexForPixel(censusReports[index].dominantColor)
        }

        val allSvgPaths = mutableListOf<String>()

        familyGroups.forEach { (groupIndex, indices) ->
            val familyPaths = indices.map { pathFragments[it] }
            val familyReports = indices.map { censusReports[it] }

            EfficiencyAudit.log(groupIndex, familyPaths)
            ColorBucketDiagnostic.logShift3Routing(groupIndex, familyPaths.size)

            val worker = CVPS_HiringDepartment.getWorkerByColorId(groupIndex)
            CVPS_Audit.logCompute(6, groupIndex)

            val data = CVPS_job6.BlendingData(familyPaths, familyReports, quantizedImage, sourceImage)
            worker.runColorTask(6, data)
            val result = data.result
            
            if (result.isNotEmpty()) {
                allSvgPaths.add("<!-- === ${getBucketLabel(groupIndex)} === -->")
                allSvgPaths.addAll(result)
            }
        }

        return allSvgPaths
    }

    private fun getBucketLabel(index: Int): String {
        return when (index) {
            0 -> "RED BLOBS"
            1 -> "GREEN BLOBS"
            2 -> "BLUE BLOBS"
            3 -> "YELLOW BLOBS"
            4 -> "CYAN BLOBS"
            5 -> "MAGENTA BLOBS"
            6 -> "WHITE BLOBS"
            7 -> "ALPHA BLOBS"
            8 -> "BLACK BLOBS"
            9 -> "GREY BLOBS"
            else -> "UNKNOWN BLOBS"
        }
    }
}
