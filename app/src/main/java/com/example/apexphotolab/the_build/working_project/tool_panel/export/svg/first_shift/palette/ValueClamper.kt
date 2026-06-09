package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.palette

import android.graphics.Color

/**
 * Job: Value Clamper.
 * Responsibility: Clamping near-black and near-white color values to true black and white while preserving alpha.
 */
object ValueClamper {

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