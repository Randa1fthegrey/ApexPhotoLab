package com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.GradientReport

/**
 * Team 0 Scout for the GREEN color group.
 * Job: Pre-scans the image for Green color smears (gradients) within the shape.
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
                isBorder = isOnBorder(path, width, height)
            )
        }
    }

    private fun isOnBorder(path: List<Point>, w: Int, h: Int): Boolean {
        val buffer = 50
        return path.any { it.x < buffer || it.x > w - buffer || it.y < buffer || it.y > h - buffer }
    }
}
