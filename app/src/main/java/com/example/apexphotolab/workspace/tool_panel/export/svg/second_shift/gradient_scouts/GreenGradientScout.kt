package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Bitmap
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.GradientReport

/**
 * Team 0 Scout for the GREEN color group.
 */
object GreenGradientScout : GradientScout {
    override val id = 1
    override fun scout(image: Bitmap): List<GradientReport> {
        return emptyList() // To be implemented with specific green-smear detection logic
    }
}
