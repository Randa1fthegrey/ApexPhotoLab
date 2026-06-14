package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.territory

import android.graphics.Color
import kotlin.math.sqrt

/**
 * Job: Color Geometric Comparator.
 * Responsibility: Performing high-precision similarity checks between pixels (The 99% Rule).
 */
object ColorGeometricComparator {

    /**
     * Checks if two pixels are solid neighbors or if there is tension.
     * Uses a 4D Euclidean distance (ARGB) to provide a "Fuzzy Buffer" (The 99% Rule).
     */
    fun areSimilar(pixelA: Int, pixelB: Int): Boolean {
        val aDiff = Color.alpha(pixelA) - Color.alpha(pixelB)
        val rDiff = Color.red(pixelA) - Color.red(pixelB)
        val gDiff = Color.green(pixelA) - Color.green(pixelB)
        val bDiff = Color.blue(pixelA) - Color.blue(pixelB)

        val distance = sqrt((aDiff * aDiff + rDiff * rDiff + gDiff * gDiff + bDiff * bDiff).toDouble())
        
        // 1. PHYSICAL SIMILARITY: If pixels are within 10 units of each other, 
        // they are solid ground, regardless of mathematical territory flips.
        if (distance < 10.0) return true

        // 2. TERRITORY SIMILARITY: If they are physically different, they must 
        // at least map to the same absolute territory to be considered solid.
        return ColorTerritoryMapper.map(pixelA) == ColorTerritoryMapper.map(pixelB)
    }
}