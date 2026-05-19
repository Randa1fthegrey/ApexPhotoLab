package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ramps

import android.graphics.Color

object AlphaRamp {
    fun generate(): List<Int> {
        val ramp = mutableListOf<Int>()
        for (i in 0 until 128) {
            val alpha = (90 * i / 127f).toInt()
            ramp.add(Color.argb(alpha, 0, 0, 0))
        }
        return ramp
    }
}
