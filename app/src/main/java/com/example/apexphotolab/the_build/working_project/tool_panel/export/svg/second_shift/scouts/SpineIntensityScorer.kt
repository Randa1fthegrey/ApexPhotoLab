package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.scouts

import android.graphics.Color
import kotlin.math.abs

/**
 * Job: Spine Intensity Scorer.
 * Responsibility: Calculating the "Purity Score" of a pixel relative to its color group territory.
 */
object SpineIntensityScorer {

    fun calculateScore(pixel: Int, groupIndex: Int): Float {
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)

        val hsv = FloatArray(3)
        Color.RGBToHSV(r, g, b, hsv)
        val hue = hsv[0]
        val sat = hsv[1]
        val val_ = hsv[2]

        return when (groupIndex) {
            0 -> 1.0f - hueDistance(hue, 0f)      // Red
            3 -> 1.0f - hueDistance(hue, 45f)     // Yellow
            1 -> 1.0f - hueDistance(hue, 120f)    // Green
            4 -> 1.0f - hueDistance(hue, 180f)    // Cyan
            2 -> 1.0f - hueDistance(hue, 240f)    // Blue
            5 -> 1.0f - hueDistance(hue, 300f)    // Magenta
            6 -> val_ * (1.0f - sat)              // White (Bright, no color)
            7 -> 1.0f - (Color.alpha(pixel) / 255.0f) // Alpha (Transparent)
            8 -> 1.0f - val_                      // Black (Dark)
            9 -> (1.0f - sat) * (0.5f - abs(val_ - 0.5f)) // Grey (Neutral middle)
            else -> 0f
        }
    }

    private fun hueDistance(h1: Float, h2: Float): Float {
        val diff = abs(h1 - h2)
        val normalized = if (diff > 180) 360 - diff else diff
        return normalized / 180f // Returns 0.0 (exact match) to 1.0 (opposite)
    }
}