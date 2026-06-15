package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg

import android.graphics.Point

/**
 * Global SVG Engine Value Utility.
 * Responsibility: Centralizing universal constants for the entire SVG generation pipeline.
 */
object val_util {

    // ==========================================
    // GEOMETRIC SENTINELS
    // ==========================================

    val SENTINEL = Point(-1, -1)

    // ==========================================
    // NAVIGATION OFFSETS
    // ==========================================

    val OFFSETS = arrayOf(
        Point(0, -1), Point(1, -1), Point(1, 0), Point(1, 1),
        Point(0, 1), Point(-1, 1), Point(-1, 0), Point(-1, -1)
    )
}
