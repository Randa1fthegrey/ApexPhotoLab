package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color

import android.graphics.Color

/**
 * Job: Color Territory Mapper.
 * Responsibility: Determining which territory a pixel belongs to based on the 4D Map rules.
 * 0: Red, 1: Green, 2: Blue, 3: Yellow, 4: Cyan, 5: Magenta,
 * 6: White, 7: Alpha, 8: Black, 9: Grey, 10: Alpha Gradient
 */
object ColorTerritoryMapper {

    fun map(pixel: Int): Int {
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

        if (v < ColorWallFences.BLACK_BRIGHTNESS_FENCE) return 8
        if (s < ColorWallFences.WHITE_SATURATION_FENCE) {
            return if (v > ColorWallFences.WHITE_BRIGHTNESS_FENCE) 6 else 9
        }

        return when {
            h >= ColorWallFences.MAGENTA_RED_FENCE || h < ColorWallFences.RED_YELLOW_FENCE -> 0
            h in ColorWallFences.RED_YELLOW_FENCE..ColorWallFences.YELLOW_GREEN_FENCE -> 3
            h in ColorWallFences.YELLOW_GREEN_FENCE..ColorWallFences.GREEN_CYAN_FENCE -> 1
            h in ColorWallFences.GREEN_CYAN_FENCE..ColorWallFences.CYAN_BLUE_FENCE -> 4
            h in ColorWallFences.CYAN_BLUE_FENCE..ColorWallFences.BLUE_MAGENTA_FENCE -> 2
            h in ColorWallFences.BLUE_MAGENTA_FENCE..ColorWallFences.MAGENTA_RED_FENCE -> 5
            else -> 9
        }
    }
}
