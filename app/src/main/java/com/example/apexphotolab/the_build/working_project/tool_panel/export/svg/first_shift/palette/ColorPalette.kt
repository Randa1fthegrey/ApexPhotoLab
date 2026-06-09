package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.palette

import android.graphics.Color

/**
 * Job: Color Palette Assembler.
 * Responsibility: Defining the 10 "Absolute Truth" colors for the entire SVG engine.
 */
object ColorPalette {

    val PALETTE: IntArray = intArrayOf(
        Color.rgb(255, 0, 0),     // 0: Red
        Color.rgb(0, 255, 0),     // 1: Green
        Color.rgb(0, 0, 255),     // 2: Blue
        Color.rgb(255, 255, 0),   // 3: Yellow
        Color.rgb(0, 255, 255),   // 4: Cyan
        Color.rgb(255, 0, 255),   // 5: Magenta
        Color.rgb(255, 255, 255), // 6: White
        Color.argb(0, 0, 0, 0),   // 7: Alpha (Transparent)
        Color.rgb(0, 0, 0),       // 8: Black
        Color.rgb(128, 128, 128)  // 9: Grey
    )

    fun getRedRange(): IntRange = 0..0
    fun getGreenRange(): IntRange = 1..1
    fun getBlueRange(): IntRange = 2..2
    fun getYellowRange(): IntRange = 3..3
    fun getCyanRange(): IntRange = 4..4
    fun getMagentaRange(): IntRange = 5..5
    fun getWhiteRange(): IntRange = 6..6
    fun getAlphaRange(): IntRange = 7..7
    fun getBlackRange(): IntRange = 8..8
    fun getGreyRange(): IntRange = 9..9
}