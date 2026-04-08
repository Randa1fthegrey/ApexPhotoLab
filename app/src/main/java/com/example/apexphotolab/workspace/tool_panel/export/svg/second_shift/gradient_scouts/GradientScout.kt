package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Bitmap
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.GradientReport

/**
 * Interface for the Team 0 (Gradient Scouts).
 * Their one job is to find gradients before structural tracing begins.
 */
interface GradientScout {
    val id: Int
    fun scout(quantizedImage: Bitmap, originalImage: Bitmap): List<GradientReport>
}
