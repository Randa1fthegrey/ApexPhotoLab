package com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift

object CensusNoiseFloor {
    private const val NOISE_THRESHOLD = 0.02f // 2% variation floor

    fun apply(totalPixels: Int, dominantCount: Int): Float {
        val rawRatio = (totalPixels - dominantCount).toFloat() / totalPixels.toFloat()
        return if (rawRatio < NOISE_THRESHOLD) 0.0f else rawRatio
    }
}
