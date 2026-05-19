package com.example.apexphotolab.the_build.working_project.tool_panel.background_remover

import android.graphics.Bitmap
import com.example.apexphotolab.the_build.working_project.tool_panel.brush_logic.ColorMatcher

/**
 * Job: Census Taker (Analyst).
 * Responsibility: Analyzes a bitmap to determine the "Ground Truth" background color.
 * 
 * Logic: Samples the four corners. If at least two corners are similar, it identifies
 * that color as the background anchor.
 */
object BackgroundCensusTaker {

    fun getBackgroundAnchor(bitmap: Bitmap): Int? {
        val width = bitmap.width
        val height = bitmap.height
        
        // Sample the 4 corners
        val corners = listOf(
            bitmap.getPixel(0, 0),
            bitmap.getPixel(width - 1, 0),
            bitmap.getPixel(0, height - 1),
            bitmap.getPixel(width - 1, height - 1)
        )
        
        // Identify the target background color (Consensus of at least 2 corners)
        for (i in corners.indices) {
            var matchCount = 0
            for (j in corners.indices) {
                if (ColorMatcher.isSimilar(corners[i], corners[j], 0.12f)) {
                    matchCount++
                }
            }
            if (matchCount >= 2) {
                return corners[i]
            }
        }

        return null
    }
}

