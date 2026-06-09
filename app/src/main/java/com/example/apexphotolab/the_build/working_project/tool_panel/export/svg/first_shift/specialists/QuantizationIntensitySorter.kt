package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.specialists

import android.graphics.Color

/**
 * Job: Intensity Sorter.
 * Responsibility: Sorting color buckets in descending order of their original pixel intensity.
 * This provides the "Slope" data for downstream shifts.
 */
object QuantizationIntensitySorter {

    fun sort(unsortedBuckets: List<List<Int>>, sourcePixels: IntArray): List<List<Int>> {
        return unsortedBuckets.map { bucket ->
            bucket.sortedByDescending { idx ->
                val p = sourcePixels[idx]
                Color.red(p) + Color.green(p) + Color.blue(p)
            }
        }
    }
}