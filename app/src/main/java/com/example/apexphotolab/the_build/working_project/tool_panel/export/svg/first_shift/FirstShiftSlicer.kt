package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.CoreChecker
import kotlin.math.ceil

/**
 * Job: Pixel Array Slicer.
 * Responsibility: Dividing the total pixel count into equal slices, one per available CPU core.
 */
object FirstShiftSlicer {

    fun createSlices(totalPixels: Int): List<IntRange> {
        val coreCount = CoreChecker.coreCount
        if (coreCount == 0) return emptyList()

        val sliceSize = ceil(totalPixels.toDouble() / coreCount).toInt()
        val slices = mutableListOf<IntRange>()

        for (i in 0 until coreCount) {
            val start = i * sliceSize
            if (start >= totalPixels) break // Avoid creating empty slices

            val end = (start + sliceSize - 1).coerceAtMost(totalPixels - 1)
            slices.add(start..end)
        }
        return slices
    }
}
