package com.example.apexphotolab.working_project.tool_panel.export.svg.first_shift.color.ramps

import android.graphics.Color

/**
 * Generates the raw, 32-shade color ramp for the White color group.
 * High-Fidelity Version: Ensures "White" is actually white (250-255).
 */
object WhiteRamp {

    fun generate(): List<Int> {
        val ramp = mutableListOf<Int>()
        for (i in 0 until 32) {
            // Start at 250 (off-white) and end at 255 (pure white).
            val shade = 250 + (5 * i / 31f).toInt()
            ramp.add(Color.rgb(shade, shade, shade))
        }
        return ramp
    }
}
