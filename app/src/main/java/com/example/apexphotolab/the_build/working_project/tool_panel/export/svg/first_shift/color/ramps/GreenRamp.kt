package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ramps

import android.graphics.Color

object GreenRamp {
    fun generate(): List<Int> {
        val ramp = mutableListOf<Int>()
        for (i in 0 until 128) {
            val shade = (255 * i / 127f).toInt()
            ramp.add(Color.rgb(0, shade, 0))
        }
        return ramp
    }
}
