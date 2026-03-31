package com.example.apexphotolab.workspace.tool_panel.export.svg.first_shift.color.ramps

import android.graphics.Color

/**
 * High-Fidelity Magenta Ramp.
 * Updated: Extended range to include bright, vibrant Pinks (#E8199B).
 */
object MagentaRamp {
    fun generate(): List<Int> {
        val ramp = mutableListOf<Int>()
        // From Dark Purple to Bright Vibrant Pink
        for (i in 0 until 32) {
            val ratio = i / 31f
            val r = (80 + (ratio * 175)).toInt().coerceIn(0, 255)
            val g = (ratio * 50).toInt().coerceIn(0, 255) // Keep green low for vibrance
            val b = (80 + (ratio * 120)).toInt().coerceIn(0, 255)
            ramp.add(Color.rgb(r, g, b))
        }
        return ramp
    }
}
