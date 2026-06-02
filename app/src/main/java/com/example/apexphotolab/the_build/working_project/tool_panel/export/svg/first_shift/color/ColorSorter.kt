package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ColorPalette
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ColorWallScale

/**
 * Job: Color Sorter (The Judge).
 * Responsibility: Categorizing every pixel into one of 10 "Absolute Truth" buckets.
 * Now acts as a wrapper for the high-precision 4D Geometric Map.
 */
object ColorSorter {

    /**
     * Maps an incoming pixel to its corresponding "Absolute Truth" color.
     */
    fun getNearestColor(pixel: Int): Int {
        val territoryId = ColorWallScale.getTerritoryId(pixel)
        
        // Handle the special 11th bucket (Alpha Gradient) by mapping to the Alpha palette base
        val paletteIndex = if (territoryId == 10) 7 else territoryId
        
        return if (paletteIndex in 0..9) {
            ColorPalette.PALETTE[paletteIndex]
        } else {
            ColorPalette.PALETTE[9] // Default to Grey
        }
    }
}
