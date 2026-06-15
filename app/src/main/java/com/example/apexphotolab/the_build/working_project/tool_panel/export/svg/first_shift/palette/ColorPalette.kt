package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.palette

import android.graphics.Color

/**
 * Job: Color Palette Assembler.
 * Responsibility: Defining the 10 "Absolute Truth" colors for the entire SVG engine.
 */
object ColorPalette {

    val PALETTE: IntArray = intArrayOf(
        val_util.COLOR_RED,
        val_util.COLOR_GREEN,
        val_util.COLOR_BLUE,
        val_util.COLOR_YELLOW,
        val_util.COLOR_CYAN,
        val_util.COLOR_MAGENTA,
        val_util.COLOR_WHITE,
        val_util.COLOR_ALPHA,
        val_util.COLOR_BLACK,
        val_util.COLOR_GREY
    )

    fun getRedRange(): IntRange = val_util.IDX_RED..val_util.IDX_RED
    fun getGreenRange(): IntRange = val_util.IDX_GREEN..val_util.IDX_GREEN
    fun getBlueRange(): IntRange = val_util.IDX_BLUE..val_util.IDX_BLUE
    fun getYellowRange(): IntRange = val_util.IDX_YELLOW..val_util.IDX_YELLOW
    fun getCyanRange(): IntRange = val_util.IDX_CYAN..val_util.IDX_CYAN
    fun getMagentaRange(): IntRange = val_util.IDX_MAGENTA..val_util.IDX_MAGENTA
    fun getWhiteRange(): IntRange = val_util.IDX_WHITE..val_util.IDX_WHITE
    fun getAlphaRange(): IntRange = val_util.IDX_ALPHA..val_util.IDX_ALPHA
    fun getBlackRange(): IntRange = val_util.IDX_BLACK..val_util.IDX_BLACK
    fun getGreyRange(): IntRange = val_util.IDX_GREY..val_util.IDX_GREY
}