package com.example.apexphotolab.the_build.working_project.tool_panel.brush_logic

/**
 * Brush Logic Value Utility.
 * Responsibility: Centralizing mathematical thresholds and constants for color matching and brush physics.
 */
object val_util {

    // ==========================================
    // COLOR MATCHING THRESHOLDS
    // ==========================================

    val BLACK_FLOOR = 0.18f
    val WHITE_CEILING = 0.82f
    val WHITE_SAT_LIMIT = 0.15f
    val ALPHA_TOLERANCE = 30

    // ==========================================
    // MATH CONSTANTS
    // ==========================================

    val MAX_RGB_DISTANCE = 441.673 // sqrt(255^2 * 3)
}
