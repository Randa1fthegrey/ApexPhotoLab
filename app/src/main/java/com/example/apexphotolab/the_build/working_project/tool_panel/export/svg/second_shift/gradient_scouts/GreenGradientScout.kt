package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.GradientReport

/**
 * Job: Green Gradient Scout.
 * Responsibility: Pre-scanning the image for gradient transitions within the Green color group.
 */
object GreenGradientScout : GradientScout {

    override val id = 1

    override fun scout(quantizedImage: Bitmap, originalImage: Bitmap): List<GradientReport> {
        val width = quantizedImage.width
        val height = quantizedImage.height
        val spines = SpineLogic.findSpine(quantizedImage, originalImage, id)

        return spines.map { path ->
            val start = path.first()
            val end = path.last()

            GradientReport(
                scoutId = id,
                path = path,
                startColor = originalImage.getPixel(start.x, start.y),
                endColor = originalImage.getPixel(end.x, end.y),
                isBorder = BorderChecker.isOnBorder(path, width, height)
            )
        }
    }
}
