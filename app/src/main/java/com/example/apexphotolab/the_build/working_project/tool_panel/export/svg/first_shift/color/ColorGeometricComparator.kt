package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color

import android.graphics.Color
import kotlin.math.sqrt

/**
 * Job: Color Geometric Comparator.
 * Responsibility: Performing high-precision similarity checks between pixels (The 99% Rule).
 */
object ColorGeometricComparator {

    /**
     * Checks if two pixels are solid neighbors or if there is tension.
     */
    fun areSimilar(pixelA: Int, pixelB: Int): Boolean {
        // First check if they even share the same territory
        if (ColorTerritoryMapper.map(pixelA) != ColorTerritoryMapper.map(pixelB)) return false
        
        val rDiff = Color.red(pixelA) - Color.red(pixelB)
        val gDiff = Color.green(pixelA) - Color.green(pixelB)
        val bDiff = Color.blue(pixelA) - Color.blue(pixelB)
        
        val distance = sqrt((rDiff * rDiff + gDiff * gDiff + bDiff * bDiff).toDouble())
        return distance < 10.0 // ~99% similarity in RGB space
    }
}
