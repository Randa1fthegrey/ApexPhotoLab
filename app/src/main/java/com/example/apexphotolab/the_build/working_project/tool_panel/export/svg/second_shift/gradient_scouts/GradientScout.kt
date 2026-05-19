package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Bitmap
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.GradientReport

/**
 * Job: Gradient Scout.
 * Responsibility: Contract for all color group gradient scouts.
 */
interface GradientScout {

    val id: Int
    fun scout(quantizedImage: Bitmap, originalImage: Bitmap): List<GradientReport>
}
