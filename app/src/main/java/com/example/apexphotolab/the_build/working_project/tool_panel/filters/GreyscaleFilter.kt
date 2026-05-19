package com.example.apexphotolab.the_build.working_project.tool_panel.filters

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

/**
 * Job: Filter Specialist (Greyscale).
 * Responsibility: Provides the logic/matrix for desaturating the canvas.
 */
object GreyscaleFilter : WorkspaceFilter {
    override val label: String = "Apply Greyscale"
    
    override val colorFilter: ColorFilter = ColorFilter.colorMatrix(
        ColorMatrix().apply { setToSaturation(0f) }
    )
}
