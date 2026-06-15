package com.example.apexphotolab.the_build.working_project.workspace

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Workspace Value Utility.
 * Responsibility: Centralizing geometry and visual constants for the editor canvas.
 */
object val_util {

    // ==========================================
    // CANVAS GEOMETRY
    // ==========================================

    val CANVAS_PADDING = 32.dp

    // ==========================================
    // BACKGROUND GRID (Checkerboard)
    // ==========================================

    val GRID_SQUARE_SIZE = 16.dp
    val GRID_COLOR_LIGHT = Color(0xFFE0E0E0)
    val GRID_COLOR_DARK = Color(0xFFD0D0D0)
}
