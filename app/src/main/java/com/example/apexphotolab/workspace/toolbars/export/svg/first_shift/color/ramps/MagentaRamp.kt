package com.example.apexphotolab.workspace.toolbars.export.svg.first_shift.color.ramps

import android.graphics.Color

/**
 * Generates the raw, 32-shade color ramp for the Magenta color group.
 * Full Vibrancy: Starts at 128.
 */
object MagentaRamp {
    fun generate(): List<Int> {
        val ramp = mutableListOf<Int>()
        for (i in 0 until 32) {
            val shade = 128 + (127 * i / 31f).toInt()
            ramp.add(Color.rgb(shade, 0, shade))
        }
        return ramp
    }
}
