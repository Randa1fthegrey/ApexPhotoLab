package com.example.apexphotolab.workspace.tool_panel.export.svg.first_shift

import android.graphics.Color
import com.example.apexphotolab.workspace.tool_panel.export.svg.first_shift.color.ColorPalette

/**
 * The "Color Sorting Hat" - High-Fidelity Build
 * Updated: Precision thresholds synchronized with ColorGroupSorter.
 */
object ColorSorter {

    private const val ALPHA_THRESHOLD = 100
    private const val BLACK_VALUE_FLOOR = 0.15f
    private const val WHITE_VALUE_CEILING = 0.80f

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
            a < ALPHA_THRESHOLD -> ColorPalette.getAlphaRange()

            // 2. Neutral Check (Synchronized with ColorGroupSorter)
            val_ > WHITE_VALUE_CEILING && sat < 0.25f -> ColorPalette.getWhiteRange()
            val_ < BLACK_VALUE_FLOOR -> ColorPalette.getBlackRange()
            sat < 0.12f -> ColorPalette.getGreyRange()

            // 3. Opaque Color Ranges
            else -> getOpaqueColorSearchRange(hue)
        }

        return findClosestColor(a, r, g, b, searchRange)
    }

    private fun getOpaqueColorSearchRange(hue: Float): IntRange {
        return when (hue) {
            in 0f..20f, in 335f..360f -> ColorPalette.getRedRange()
            in 20f..75f -> ColorPalette.getYellowRange()
            in 75f..160f -> ColorPalette.getGreenRange()
            in 160f..195f -> ColorPalette.getCyanRange()
            in 195f..265f -> ColorPalette.getBlueRange()
            in 265f..335f -> ColorPalette.getMagentaRange()
            else -> ColorPalette.getGreyRange()
        }
    }

    private fun findClosestColor(a: Int, r: Int, g: Int, b: Int, range: IntRange): Int {
        var minSquaredDistance = Double.MAX_VALUE
        var bestColor = 0
        val palette = ColorPalette.PALETTE

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
}
