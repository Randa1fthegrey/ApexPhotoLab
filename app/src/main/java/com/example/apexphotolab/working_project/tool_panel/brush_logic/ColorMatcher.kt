package com.example.apexphotolab.working_project.tool_panel.brush_logic

import android.graphics.Color
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * The "Color Matcher".
 * Implements Euclidean distance math with Universal Luma Neutralization Zones.
 *
 * Logic:
 * 1. Deep Black Zone (< 18%): All colors match (Hue doesn't matter).
 * 2. Pure White Zone (> 82%): All pale colors match (High brightness, Low Saturation).
 * 3. Vibrant Zone: Strict Euclidean distance math.
 */
object ColorMatcher {

    private const val BLACK_FLOOR = 0.18f // 18% Brightness
    private const val WHITE_CEILING = 0.82f // 82% Brightness
    private const val WHITE_SAT_LIMIT = 0.15f // Max saturation to be in the "White Zone"

    fun isSimilar(colorA: Int, colorB: Int, threshold: Float): Boolean {
        // 0. Alpha Check
        val a1 = Color.alpha(colorA)
        val a2 = Color.alpha(colorB)
        if (a1 < 30 && a2 < 30) return true // Both nearly transparent
        if ((a1 < 30) != (a2 < 30)) return false // One transparent, one not

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
        if (valA <= BLACK_FLOOR && valB <= BLACK_FLOOR) return true

        // 2. THE WHITE ZONE (Neutralization)
        // If both are extremely bright and low saturation (pale), they match.
        if (valA >= WHITE_CEILING && satA <= WHITE_SAT_LIMIT &&
            valB >= WHITE_CEILING && satB <= WHITE_SAT_LIMIT) return true

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

        val normalizedDistance = distance / 441.673

        return normalizedDistance <= threshold
    }
}