package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift

import android.graphics.Color
import android.util.Log

/**
 * The "Color Group Sorter".
 * High-Fidelity Update: Corrected Hue Ranges to prevent color capture and hangs.
 */
object ColorGroupSorter {

    private const val TAG = "SVG"
    private const val ALPHA_THRESHOLD = 100
    private const val BLACK_VALUE_FLOOR = 0.25f // Sync'd with ColorSorter

    fun groupPixelIndices(pixels: IntArray): List<List<Int>> {
        val groups = List(10) { mutableListOf<Int>() }

        pixels.forEachIndexed { index, pixel ->
            val groupIndex = getGroupIndexForPixel(pixel)
            if (groupIndex in 0..9) {
                groups[groupIndex].add(index)
            }
        }

        val groupNames = listOf("Red", "Green", "Blue", "Yellow", "Cyan", "Magenta", "White", "Alpha", "Black", "Grey")
        val summary = groups.mapIndexed { i, list -> "${groupNames[i]}: ${list.size}" }.joinToString(", ")
        Log.d(TAG, "[Hiring Department] :: Handoff Summary: $summary")

        return groups
    }

    /**
     * Determines the group index for a given pixel color.
     */
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

        // 1. Alpha Check
        if (a < ALPHA_THRESHOLD) return 7

        // 2. Neutral Check (with Luminosity Floor)
        if (sat < 0.15f || val_ < BLACK_VALUE_FLOOR) {
            return when {
                val_ > 0.9f -> 6 // White
                val_ < BLACK_VALUE_FLOOR -> 8 // Black
                else -> 9         // Grey
            }
        }

        // 3. Opaque Color Check
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
