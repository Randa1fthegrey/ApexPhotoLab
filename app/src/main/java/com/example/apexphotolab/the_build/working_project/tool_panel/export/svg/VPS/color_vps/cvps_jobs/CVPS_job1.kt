package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Color

/**
 * Job: CVPS Job 1 - Ramps.
 * Responsibility: Generating color ramps for all 10 Absolute Truth colors.
 */
object CVPS_job1 {

    data class RampData(var result: List<Int> = emptyList())

    fun execute(colorId: Int, data: Any?) {
        val rData = data as? RampData ?: return
        val ramp = mutableListOf<Int>()

        when (colorId) {
            0 -> { // RED
                for (i in 0 until 128) {
                    val shade = (255 * i / 127f).toInt()
                    ramp.add(Color.rgb(shade, 0, 0))
                }
            }
            1 -> { // GREEN
                for (i in 0 until 128) {
                    val shade = (255 * i / 127f).toInt()
                    ramp.add(Color.rgb(0, shade, 0))
                }
            }
            2 -> { // BLUE
                for (i in 0 until 128) {
                    val shade = (255 * i / 127f).toInt()
                    ramp.add(Color.rgb(0, 0, shade))
                }
            }
            3 -> { // YELLOW
                for (i in 0 until 128) {
                    val shade = (255 * i / 127f).toInt()
                    ramp.add(Color.rgb(shade, shade, 0))
                }
            }
            4 -> { // CYAN
                for (i in 0 until 128) {
                    val shade = (255 * i / 127f).toInt()
                    ramp.add(Color.rgb(0, shade, shade))
                }
            }
            5 -> { // MAGENTA
                for (i in 0 until 128) {
                    val shade = (255 * i / 127f).toInt()
                    ramp.add(Color.rgb(shade, 0, shade))
                }
            }
            6 -> { // WHITE
                for (i in 0 until 128) {
                    val shade = 200 + (55 * i / 127f).toInt()
                    ramp.add(Color.rgb(shade, shade, shade))
                }
            }
            7 -> { // ALPHA
                for (i in 0 until 128) {
                    val alpha = (90 * i / 127f).toInt()
                    ramp.add(Color.argb(alpha, 0, 0, 0))
                }
            }
            8 -> { // BLACK
                for (i in 0 until 128) {
                    val shade = (50 * i / 127f).toInt()
                    ramp.add(Color.rgb(shade, shade, shade))
                }
            }
            9 -> { // GREY
                for (i in 0 until 128) {
                    val shade = 50 + (150 * i / 127f).toInt()
                    ramp.add(Color.rgb(shade, shade, shade))
                }
            }
        }
        rData.result = ramp
    }
}