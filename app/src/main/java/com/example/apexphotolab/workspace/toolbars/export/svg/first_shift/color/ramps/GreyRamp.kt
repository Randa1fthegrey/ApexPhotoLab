package com.example.apexphotolab.workspace.toolbars.export.svg.first_shift.color.ramps

import android.graphics.Color

/**
 * Generates the raw, 32-shade color ramp for the dedicated Greyscale (middle grey) color group.
 * Vibrant Version: Focuses on the middle range to ensure distinct identity.
 */
object GreyRamp {

    fun generate(): List<Int> {
        val ramp = mutableListOf<Int>()
        for (i in 0 until 32) {
            // Focuses on the middle range (roughly 70 to 190) to avoid overlap with Black and White ramps.
            val shade = 70 + (120 * i / 31f).toInt()
            ramp.add(Color.rgb(shade, shade, shade))
        }
        return ramp
    }
}
