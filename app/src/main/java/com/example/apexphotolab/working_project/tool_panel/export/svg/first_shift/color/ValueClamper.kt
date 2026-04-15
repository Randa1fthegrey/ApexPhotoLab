package com.example.apexphotolab.working_project.tool_panel.export.svg.first_shift.color

import android.graphics.Color

/**
 * A single-responsibility utility for applying artistic clamping to color values.
 * Fixed Version: Preserves Alpha channel during clamping.
 */
object ValueClamper {

    /**
     * Applies a "True Black/White" clamping while preserving transparency.
     */
    fun apply(rawRamp: List<Int>): List<Int> {
        return rawRamp.map { color ->
            val a = Color.alpha(color)
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            
            when {
                // Preserve transparency by using argb instead of rgb
                r <= 15 && g <= 15 && b <= 15 -> Color.argb(a, 0, 0, 0)      // True Black (with original Alpha)
                r >= 250 && g >= 250 && b >= 250 -> Color.argb(a, 255, 255, 255) // True White (with original Alpha)
                else -> color
            }
        }
    }
}
