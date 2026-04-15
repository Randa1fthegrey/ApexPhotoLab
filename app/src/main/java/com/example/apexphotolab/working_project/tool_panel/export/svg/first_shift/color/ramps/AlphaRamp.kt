package com.example.apexphotolab.working_project.tool_panel.export.svg.first_shift.color.ramps

import android.graphics.Color

/**
 * Generates the raw, 32-shade alpha ramp.
 * Fixed: Capped at 90 alpha to ensure it always stays below the 100 threshold.
 */
object AlphaRamp {

    fun generate(): List<Int> {
        val ramp = mutableListOf<Int>()
        for (i in 0 until 32) {
            // Max alpha 90 ensures these pixels always stay in the Alpha Team.
            val alpha = (90 * i / 31f).toInt()
            ramp.add(Color.argb(alpha, 0, 0, 0))
        }
        return ramp
    }
}
