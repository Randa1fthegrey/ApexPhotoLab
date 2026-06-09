package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers

/**
 * Job: Census Noise Floor.
 * Responsibility: Suppressing insignificant color variation ratios below the noise threshold.
 */
object CensusNoiseFloor {

    fun apply(totalPixels: Int, dominantCount: Int): Float {
        val rawRatio = (totalPixels - dominantCount).toFloat() / totalPixels.toFloat()
        return if (rawRatio < NOISE_THRESHOLD) 0.0f else rawRatio
    }

    private const val NOISE_THRESHOLD = 0.02f // 2% variation floor
}