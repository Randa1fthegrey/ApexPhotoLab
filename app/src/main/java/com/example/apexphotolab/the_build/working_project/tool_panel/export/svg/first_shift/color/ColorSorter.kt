package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color

import android.graphics.Color
import kotlin.math.abs

/**
 * Job: Color Sorter (The Traffic Cop).
 * Responsibility: Categorizing every pixel into one of 10 "Absolute Truth" buckets based on RGB dominance and a 126-threshold.
 */
object ColorSorter {

    /**
     * Maps an incoming pixel to its corresponding "Absolute Truth" color.
     */
    fun getNearestColor(pixel: Int): Int {
        val a = Color.alpha(pixel)
        if (a < ALPHA_THRESHOLD) return ColorPalette.PALETTE[7] // Alpha

        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)

        // 1. Check for Neutrals (White, Black, Grey)
        val max = maxOf(r, maxOf(g, b))
        val min = minOf(r, minOf(g, b))
        val diff = max - min

        // If RGB values are very close together (low saturation), it's a neutral
        if (diff < NEUTRAL_SENSITIVITY) {
            return when {
                max > WHITE_THRESHOLD -> ColorPalette.PALETTE[6] // White
                max < BLACK_THRESHOLD -> ColorPalette.PALETTE[8] // Black
                else -> ColorPalette.PALETTE[9] // Grey
            }
        }

        // 2. Identify Dominance based on the 126-threshold (> 125)
        val isRedDominant = r > DOMINANCE_THRESHOLD
        val isGreenDominant = g > DOMINANCE_THRESHOLD
        val isBlueDominant = b > DOMINANCE_THRESHOLD

        // Combinations (Secondaries)
        if (isRedDominant && isGreenDominant) return ColorPalette.PALETTE[3] // Yellow
        if (isGreenDominant && isBlueDominant) return ColorPalette.PALETTE[4] // Cyan
        if (isRedDominant && isBlueDominant) return ColorPalette.PALETTE[5] // Magenta

        // Primaries
        if (isRedDominant) return ColorPalette.PALETTE[0]   // Red
        if (isGreenDominant) return ColorPalette.PALETTE[1] // Green
        if (isBlueDominant) return ColorPalette.PALETTE[2]  // Blue

        // 3. Fallback for Darker/Subdued colors (check secondaries first, then primaries)
        val secondaryThreshold = 15
        val rgDiff = abs(r - g)
        val gbDiff = abs(g - b)
        val rbDiff = abs(r - b)

        return when {
            rgDiff < secondaryThreshold && r > b && g > b -> ColorPalette.PALETTE[3] // Subdued Yellow
            gbDiff < secondaryThreshold && g > r && b > r -> ColorPalette.PALETTE[4] // Subdued Cyan
            rbDiff < secondaryThreshold && r > g && b > g -> ColorPalette.PALETTE[5] // Subdued Magenta
            r >= g && r >= b -> ColorPalette.PALETTE[0] // Subdued Red
            g >= r && g >= b -> ColorPalette.PALETTE[1] // Subdued Green
            else -> ColorPalette.PALETTE[2]             // Subdued Blue
        }
    }

    private const val ALPHA_THRESHOLD = 1 // Strictly zero is Alpha bucket
    private const val DOMINANCE_THRESHOLD = 125 // 126 and above is dominant
    private const val WHITE_THRESHOLD = 230
    private const val BLACK_THRESHOLD = 40
    private const val NEUTRAL_SENSITIVITY = 20 // Tighter sensitivity to prevent Blue bias
}
