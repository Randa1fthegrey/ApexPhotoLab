package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift

import android.graphics.Color

/**
 * The "Color Group Sorter".
 * Precision Update: Absolute White Sovereignty. 
 * Only 100% pure white is White. This eliminates "clipping" and "dusting" on outlines.
 */
object ColorGroupSorter {

    private const val ALPHA_THRESHOLD = 100
    private const val BLACK_VALUE_FLOOR = 0.15f
    private const val WHITE_VALUE_CEILING = 1.0f // Only pure #FFFFFF is White.

    fun groupPixelIndices(pixels: IntArray, width: Int, height: Int): List<List<Int>> {
        val groups = List(10) { mutableListOf<Int>() }
        
        pixels.forEachIndexed { index, pixel ->
            val groupIndex = getGroupIndexForPixel(pixel)
            if (groupIndex in 0..9) {
                groups[groupIndex].add(index)
            }
        }
        return groups
    }

    fun getGroupIndexForPixel(pixel: Int): Int {
        val a = Color.alpha(pixel)
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)

        val hsv = FloatArray(3)
        Color.RGBToHSV(r, g, b, hsv)
        val hue = hsv[0]
        val sat = hsv[1]
        val val_ = hsv[2]

        if (a < ALPHA_THRESHOLD) return 7

        // ABSOLUTE NEUTRAL LOGIC
        // If it's pure white, it's White. If it's very dark, it's Black.
        if (val_ >= WHITE_VALUE_CEILING && sat < 0.01f) return 6
        if (val_ < BLACK_VALUE_FLOOR) return 8

        // If it's low saturation, it's Grey (captures the circle outline perfectly)
        if (sat < 0.15f) return 9

        return when (hue) {
            in 0f..20f, in 335f..360f -> 0 // Red
            in 20f..75f -> 3               // Yellow
            in 75f..160f -> 1              // Green
            in 160f..195f -> 4             // Cyan
            in 195f..265f -> 2             // Blue
            in 265f..335f -> 5             // Magenta
            else -> 9                      // Fallback
        }
    }
}
