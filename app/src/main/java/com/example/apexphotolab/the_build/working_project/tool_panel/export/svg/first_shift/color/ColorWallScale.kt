package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color

import android.graphics.Color

/**
 * Job: Color Wall Scale (The 3D Geometric Map).
 * Responsibility: Defining the absolute mathematical boundaries for the 11 territories of the world.
 * Based on high-precision boundary clicks from the Color Wheel Tool.
 */
object ColorWallScale {

    // --- 1. NEUTRAL FENCES (The Core) ---
    const val WHITE_SATURATION_FENCE = 5.0f // The "Inner Circle"
    const val WHITE_BRIGHTNESS_FENCE = 0.90f // The "North Pole"
    const val BLACK_BRIGHTNESS_FENCE = 0.20f // The "Outer Void/Basement"

    // --- 2. HUE SLICE FENCES (The Pie) ---
    // Boundaries defined by the "One value off" rule to ensure Zero Gap.
    const val RED_YELLOW_FENCE = 40.0f
    const val YELLOW_GREEN_FENCE = 80.0f
    const val GREEN_CYAN_FENCE = 152.0f
    const val CYAN_BLUE_FENCE = 201.0f
    const val BLUE_MAGENTA_FENCE = 269.0f
    const val MAGENTA_RED_FENCE = 337.0f

    /**
     * Determines the Territory ID for a given pixel based on the 4D Map.
     * 0: Red, 1: Green, 2: Blue, 3: Yellow, 4: Cyan, 5: Magenta,
     * 6: White, 7: Alpha, 8: Black, 9: Grey, 10: Alpha Gradient
     */
    fun getTerritoryId(pixel: Int): Int {
        val a = Color.alpha(pixel)
        if (a == 0) return 7 // THE VOID (Alpha)
        if (a < 255) return 10 // THE GHOST (Alpha Gradient)

        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)

        val hsv = FloatArray(3)
        Color.RGBToHSV(r, g, b, hsv)
        val h = hsv[0]
        val s = hsv[1] * 100f
        val v = hsv[2]

        if (v < BLACK_BRIGHTNESS_FENCE) return 8
        if (s < WHITE_SATURATION_FENCE) {
            return if (v > WHITE_BRIGHTNESS_FENCE) 6 else 9
        }

        return when {
            h >= MAGENTA_RED_FENCE || h < RED_YELLOW_FENCE -> 0
            h in RED_YELLOW_FENCE..YELLOW_GREEN_FENCE -> 3
            h in YELLOW_GREEN_FENCE..GREEN_CYAN_FENCE -> 1
            h in GREEN_CYAN_FENCE..CYAN_BLUE_FENCE -> 4
            h in CYAN_BLUE_FENCE..BLUE_MAGENTA_FENCE -> 2
            h in BLUE_MAGENTA_FENCE..MAGENTA_RED_FENCE -> 5
            else -> 9
        }
    }

    /**
     * The 99% Rule: Checks if two pixels are solid neighbors or if there is tension.
     */
    fun isSolidGround(pixelA: Int, pixelB: Int): Boolean {
        if (getTerritoryId(pixelA) != getTerritoryId(pixelB)) return false
        
        val rDiff = Color.red(pixelA) - Color.red(pixelB)
        val gDiff = Color.green(pixelA) - Color.green(pixelB)
        val bDiff = Color.blue(pixelA) - Color.blue(pixelB)
        
        val distance = Math.sqrt((rDiff * rDiff + gDiff * gDiff + bDiff * bDiff).toDouble())
        return distance < 10.0 // ~99% similarity in RGB space
    }
}
