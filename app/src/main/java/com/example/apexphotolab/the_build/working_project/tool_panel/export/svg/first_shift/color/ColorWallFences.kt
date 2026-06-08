package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color

/**
 * Job: Color Wall Fence Definitions.
 * Responsibility: Holding the absolute mathematical boundaries for the 11 territories of the map.
 */
object ColorWallFences {

    // --- 1. NEUTRAL FENCES (The Core) ---
    const val WHITE_SATURATION_FENCE = 5.0f // The "Inner Circle"
    const val WHITE_BRIGHTNESS_FENCE = 0.90f // The "North Pole"
    const val BLACK_BRIGHTNESS_FENCE = 0.20f // The "Outer Void/Basement"

    // --- 2. HUE SLICE FENCES (The Pie) ---
    const val RED_YELLOW_FENCE = 40.0f
    const val YELLOW_GREEN_FENCE = 80.0f
    const val GREEN_CYAN_FENCE = 152.0f
    const val CYAN_BLUE_FENCE = 201.0f
    const val BLUE_MAGENTA_FENCE = 269.0f
    const val MAGENTA_RED_FENCE = 337.0f
}
