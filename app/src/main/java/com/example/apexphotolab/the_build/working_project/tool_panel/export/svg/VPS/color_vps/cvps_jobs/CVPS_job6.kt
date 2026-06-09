package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.VPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.vps_jobs.VPS_job4
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.VPS_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.SolidFillGenerator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusReport

/**
 * Job: CVPS Job 6 - Blending Managers.
 * Responsibility: Routing each path to either solid fill or gradient blending based on complexity score.
 */
object CVPS_job6 {

    data class BlendingData(
        val paths: List<List<Point>>,
        val reports: List<CensusReport>,
        val quantizedImage: Bitmap,
        val sourceImage: Bitmap,
        var result: List<String> = emptyList()
    )

    suspend fun execute(colorId: Int, data: Any?) {
        val bData = data as? BlendingData ?: return

        if (colorId == 7) { // ALPHA
            // AUTO-ERASE: We write nothing to the final SVG document for Alpha.
            bData.result = emptyList()
            return
        }

        val svgResults = mutableListOf<String>()

        bData.paths.forEachIndexed { index, path ->
            val report = bData.reports[index]

            if (report.complexityScore == 0) {
                svgResults.add(SolidFillGenerator.generate(listOf(path), report.dominantColor))
            } else {
                // VPS CALL: Hand off to Job 4 (Ladder Sampling)
                val worker = VPS_HiringDepartment.getWorkerById(index + 1)
                VPS_Audit.logCompute(4, worker.id)

                val taskData = VPS_job4.BlendingData(path, report, bData.quantizedImage, bData.sourceImage)
                worker.runTask(4, taskData)

                if (taskData.svgResult.isNotEmpty()) {
                    svgResults.add(taskData.svgResult)
                }
            }
        }

        bData.result = svgResults
    }
}