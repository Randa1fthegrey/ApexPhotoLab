package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Bitmap
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.scouts.GradientReport
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.scouts.BorderChecker
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.scouts.SpineLogic

/**
 * Job: CVPS Job 3 - Gradient Scouts.
 * Responsibility: Pre-scanning the image for gradient transitions within a color group.
 */
object CVPS_job3 {

    data class ScoutData(
        val quantizedImage: Bitmap,
        val originalImage: Bitmap,
        var result: List<GradientReport> = emptyList()
    )

    fun execute(colorId: Int, data: Any?) {
        val sData = data as? ScoutData ?: return
        val width = sData.quantizedImage.width
        val height = sData.quantizedImage.height
        val spines = SpineLogic.findSpine(sData.quantizedImage, sData.originalImage, colorId)

        sData.result = spines.map { path ->
            val start = path.first()
            val end = path.last()

            GradientReport(
                scoutId = colorId,
                path = path,
                startColor = sData.originalImage.getPixel(start.x, start.y),
                endColor = sData.originalImage.getPixel(end.x, end.y),
                isBorder = BorderChecker.isOnBorder(path, width, height)
            )
        }
    }
}