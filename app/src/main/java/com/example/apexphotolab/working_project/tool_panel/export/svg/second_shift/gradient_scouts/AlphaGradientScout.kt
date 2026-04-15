package com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.GradientReport

/**
 * Team 0 Scout for the ALPHA color group.
 */
object AlphaGradientScout : GradientScout {
    override val id = 7

    override fun scout(quantizedImage: Bitmap, originalImage: Bitmap): List<GradientReport> {
        val width = quantizedImage.width
        val height = quantizedImage.height
        val spines = SpineLogic.findSpine(quantizedImage, originalImage, id)
        return spines.map { path ->
            GradientReport(id, path, originalImage.getPixel(path.first().x, path.first().y), originalImage.getPixel(path.last().x, path.last().y), isOnBorder(path, width, height))
        }
    }

    private fun isOnBorder(path: List<Point>, w: Int, h: Int): Boolean {
        val buffer = 50
        return path.any { it.x < buffer || it.x > w - buffer || it.y < buffer || it.y > h - buffer }
    }
}
