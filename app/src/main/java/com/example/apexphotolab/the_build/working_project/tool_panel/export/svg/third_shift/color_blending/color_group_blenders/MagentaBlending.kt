package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.color_blending.color_group_blenders

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.color_blending.BlenderHiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.SolidFillGenerator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.CensusReport

/**
 * Job: Blending manager for the Magenta color group.
 * Responsibility: Routing each path to either solid fill or gradient blending based on complexity score.
 */
object MagentaBlending {

    fun process(
        paths: List<List<Point>>,
        reports: List<CensusReport>,
        quantizedImage: Bitmap,
        sourceImage: Bitmap
    ): List<String> {
        val svgResults = mutableListOf<String>()

        paths.forEachIndexed { index, path ->
            val report = reports[index]

            if (report.complexityScore == 0) {
                svgResults.add(SolidFillGenerator.generate(listOf(path), report.dominantColor))
            } else {
                val worker = BlenderHiringDepartment.hireWorker(index)
                svgResults.add(worker.blend(path, report, quantizedImage, sourceImage))
            }
        }

        return svgResults
    }
}
