package com.example.apexphotolab.working_project.tool_panel.export.svg.first_shift.color.ramps

import android.graphics.Color

/**
 * Generates the raw, 32-shade color ramp for the Black color group.
 * Vibrant Version: Focuses only on dark greys and pure black.
 */
object BlackRamp {

    fun generate(): List<Int> {
        val ramp = mutableListOf<Int>()
        for (i in 0 until 32) {
            // End shade at 60 to produce only dark greys and black.
            val shade = (60 * i / 31f).toInt()
            ramp.add(Color.rgb(shade, shade, shade))
        }
        return ramp
    }
}
