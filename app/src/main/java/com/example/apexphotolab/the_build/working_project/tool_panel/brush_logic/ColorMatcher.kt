package com.example.apexphotolab.the_build.working_project.tool_panel.brush_logic

import android.graphics.Color
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Job: Mathematical Worker (Color Comparison).
 * Responsibility: Determines if two colors are "similar enough" based on Euclidean distance
 * and specialized neutralization zones for shadows and highlights.
 *
 * Purified: Pure logic; tool-agnostic. Implements high-performance math for 
 * selection-based tools (Magic Eraser, BG Remover, etc.).
 *
 * Logic:
 * 1. THE BLACK ZONE (Neutralization): If both colors are dark enough (< 18% brightness),
 *    they are considered a match regardless of hue.
 * 2. THE WHITE ZONE (Neutralization): If both colors are bright (> 82%) and pale,
 *    they are considered a match.
 * 3. THE VIBRANT ZONE (Euclidean Distance): Standard RGB distance calculation.
 */
object ColorMatcher {

    fun isSimilar(colorA: Int, colorB: Int, threshold: Float): Boolean {
        // 0. Alpha Check
        val a1 = Color.alpha(colorA)
        val a2 = Color.alpha(colorB)
        if (a1 < val_util.ALPHA_TOLERANCE && a2 < val_util.ALPHA_TOLERANCE) return true // Both nearly transparent
        if ((a1 < val_util.ALPHA_TOLERANCE) != (a2 < val_util.ALPHA_TOLERANCE)) return false // One transparent, one not

        // Get HSV for Luma checks
        val hsvA = FloatArray(3)
        val hsvB = FloatArray(3)
        Color.RGBToHSV(Color.red(colorA), Color.green(colorA), Color.blue(colorA), hsvA)
        Color.RGBToHSV(Color.red(colorB), Color.green(colorB), Color.blue(colorB), hsvB)

        val valA = hsvA[2]
        val satA = hsvA[1]
        val valB = hsvB[2]
        val satB = hsvB[1]

        // 1. THE BLACK ZONE (Neutralization)
        // If both are extremely dark, they are "The Same" regardless of subtle color shifts.
        if (valA <= val_util.BLACK_FLOOR && valB <= val_util.BLACK_FLOOR) return true

        // 2. THE WHITE ZONE (Neutralization)
        // If both are extremely bright and low saturation (pale), they match.
        if (valA >= val_util.WHITE_CEILING && satA <= val_util.WHITE_SAT_LIMIT &&
            valB >= val_util.WHITE_CEILING && satB <= val_util.WHITE_SAT_LIMIT) return true

        // 3. THE VIBRANT ZONE (Euclidean Distance)
        val r1 = Color.red(colorA)
        val g1 = Color.green(colorA)
        val b1 = Color.blue(colorA)

        val r2 = Color.red(colorB)
        val g2 = Color.green(colorB)
        val b2 = Color.blue(colorB)

        val distance = sqrt(
            (r1 - r2).toDouble().pow(2) +
                    (g1 - g2).toDouble().pow(2) +
                    (b1 - b2).toDouble().pow(2)
        )

        val normalizedDistance = distance / val_util.MAX_RGB_DISTANCE

        return normalizedDistance <= threshold
    }
}
