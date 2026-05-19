package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ramps

import android.graphics.Color

object GreyRamp {
    fun generate(): List<Int> {
        val ramp = mutableListOf<Int>()
        for (i in 0 until 128) {
            val shade = 50 + (150 * i / 127f).toInt()
            ramp.add(Color.rgb(shade, shade, shade))
        }
        return ramp
    }
}
