package com.example.apexphotolab.workspace.toolbars.export.svg.first_shift

import com.example.apexphotolab.workspace.toolbars.export.svg.utils.CoreChecker
import kotlin.math.ceil

/**
 * The "Slicer" for the first shift (Color Quantization).
 * Divides the total pixel count into a number of slices equal to the available CPU cores.
 */
object FirstShiftSlicer {

    /**
     * Creates a list of IntRanges, where each range represents the indices
     * of the source pixel array that a single worker should process.
     *
     * @param totalPixels The total number of pixels in the image.
     * @return A list of IntRange objects, one for each available core.
     */
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