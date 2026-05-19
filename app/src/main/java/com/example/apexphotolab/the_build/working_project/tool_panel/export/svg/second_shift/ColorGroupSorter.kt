package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ColorPalette
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ColorSorter

/**
 * Job: Color Group Sorter.
 * Responsibility: Sorting pixels into one of the 10 "Absolute Truth" buckets for processing.
 */
object ColorGroupSorter {

    /**
     * Groups pixel indices by their quantized color index.
     */
    fun groupPixelIndices(pixels: IntArray, width: Int, height: Int): List<List<Int>> {
        val groups = List(10) { mutableListOf<Int>() }

        pixels.forEachIndexed { index, pixel ->
            val groupIndex = getGroupIndexForPixel(pixel)
            if (groupIndex in 0..9) {
                groups[groupIndex].add(index)
            }
        }
        return groups
    }

    /**
     * Identifies which of the 10 palette buckets a pixel belongs to.
     * Expects a quantized pixel, but handles shades by re-categorizing if necessary.
     */
    fun getGroupIndexForPixel(pixel: Int): Int {
        val exactIndex = ColorPalette.PALETTE.indexOf(pixel)
        if (exactIndex != -1) return exactIndex
        
        // If not exact (e.g. from the 1280-shade palette), use the Sorter to find the bucket
        val nearestBucketColor = ColorSorter.getNearestColor(pixel)
        return ColorPalette.PALETTE.indexOf(nearestBucketColor)
    }
}
