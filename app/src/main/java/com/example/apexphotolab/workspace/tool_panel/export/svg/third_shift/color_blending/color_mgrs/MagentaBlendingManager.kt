package com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.color_blending.color_mgrs

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.color_blending.BlenderHiringDepartment
import com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist.SolidFillGenerator
import com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.CensusReport

/**
 * Blending Manager for the MAGENTA color family.
 */
object MagentaBlendingManager {

    fun process(
        paths: List<List<Point>>,
        reports: List<CensusReport>,
        sourceImage: Bitmap
    ): List<String> {
        val svgResults = mutableListOf<String>()

        paths.forEachIndexed { index, path ->
            val report = reports[index]

            if (report.complexityScore == 0) {
                svgResults.add(SolidFillGenerator.generate(listOf(path), report.dominantColor))
            } else {
                val worker = BlenderHiringDepartment.hireWorker(index)
                svgResults.add(worker.blend(path, report, sourceImage))
            }
        }

        return svgResults
    }
}
