package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure

/**
 * Job: Second Shift Noise Filter.
 * Responsibility: Providing a clean, stable group index for any pixel,
 * smoothing out JPG artifacts or transparency fluctuations.
 */
object SecondShiftNoiseFilter {

    /**
     * Resolves the "Absolute Truth" group for a pixel.
     * Maps Alpha Gradient (10) to Alpha Void (7) to ensure background stability.
     */
    fun getCleanGroup(pixel: Int): Int {
        val rawGroup = ColorGroupSorter.getGroupIndexForPixel(pixel)

        // Treat all Alpha/Transparency as a single "Void" territory (7)
        return if (rawGroup == 10) 7 else rawGroup
    }
}