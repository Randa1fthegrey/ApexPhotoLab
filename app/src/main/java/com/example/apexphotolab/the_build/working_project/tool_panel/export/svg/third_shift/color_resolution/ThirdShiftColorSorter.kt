package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.color_resolution

import android.graphics.Color

/**
 * Job: Third Shift Color Sorter.
 * Responsibility: Mapping any pixel to its nearest color in the Third Shift palette,
 * with thresholds synchronized to ColorGroupSorter to prevent fill spill.
 */
object ThirdShiftColorSorter {

    fun getNearestColor(pixel: Int): Int {
        val a = Color.alpha(pixel)
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)

        val hsv = FloatArray(3)
        Color.RGBToHSV(r, g, b, hsv)
        val hue = hsv[0]
        val sat = hsv[1]
        val val_ = hsv[2]

        val searchRange = when {
            // 1. Alpha Range
            a < ALPHA_THRESHOLD -> ThirdShiftColorPalette.getAlphaRange()

            // 2. Neutral Range
            val_ > WHITE_VALUE_CEILING && sat < 0.05f -> ThirdShiftColorPalette.getWhiteRange()
            val_ < BLACK_VALUE_FLOOR -> ThirdShiftColorPalette.getBlackRange()
            sat < 0.15f -> ThirdShiftColorPalette.getGreyRange()

            // 3. Opaque Color Ranges
            else -> getOpaqueColorSearchRange(hue)
        }

        return findClosestColor(a, r, g, b, searchRange)
    }

    private fun getOpaqueColorSearchRange(hue: Float): IntRange {
        return when (hue) {
            in 0f..20f, in 335f..360f -> ThirdShiftColorPalette.getRedRange()
            in 20f..75f -> ThirdShiftColorPalette.getYellowRange()
            in 75f..160f -> ThirdShiftColorPalette.getGreenRange()
            in 160f..195f -> ThirdShiftColorPalette.getCyanRange()
            in 195f..265f -> ThirdShiftColorPalette.getBlueRange()
            in 265f..335f -> ThirdShiftColorPalette.getMagentaRange()
            else -> ThirdShiftColorPalette.getGreyRange()
        }
    }

    private fun findClosestColor(a: Int, r: Int, g: Int, b: Int, range: IntRange): Int {
        var minSquaredDistance = Double.MAX_VALUE
        var bestColor = 0
        val palette = ThirdShiftColorPalette.PALETTE

        for (i in range) {
            val paletteColor = palette[i]
            val pa = Color.alpha(paletteColor)
            val pr = Color.red(paletteColor)
            val pg = Color.green(paletteColor)
            val pb = Color.blue(paletteColor)

            val da = (a - pa).toDouble()
            val dr = (r - pr).toDouble()
            val dg = (g - pg).toDouble()
            val db = (b - pb).toDouble()

            val squaredDistance = (da * da * 1.5) + (dr * dr) + (dg * dg) + (db * db)

            if (squaredDistance < minSquaredDistance) {
                minSquaredDistance = squaredDistance
                bestColor = paletteColor
            }
        }
        return bestColor
    }

    private const val ALPHA_THRESHOLD = 1
    private const val BLACK_VALUE_FLOOR = 0.15f
    private const val WHITE_VALUE_CEILING = 0.95f
}