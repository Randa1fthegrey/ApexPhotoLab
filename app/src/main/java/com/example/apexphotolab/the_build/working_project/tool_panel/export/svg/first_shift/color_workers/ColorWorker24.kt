package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color_workers

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ColorSorter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ColorPalette
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.FirstShiftWorker
import kotlinx.coroutines.ensureActive
import java.nio.ByteBuffer
import kotlin.coroutines.coroutineContext

/**
 * Job: First Shift Color Worker #24.
 * Responsibility: Processing a pixel slice and sorting pixels into 10 "Absolute Truth" buckets.
 */
object ColorWorker24 : FirstShiftWorker {

    override val id = 24

    override suspend fun processSlice(
        sourcePixels: IntArray,
        targetPixels: IntArray,
        slice: IntRange,
        vramSlot: ByteBuffer
    ): List<List<Int>> {
        val buckets = List(10) { mutableListOf<Int>() }
        
        for (i in slice) {
            if (i % CANCELLATION_CHECK_INTERVAL == 0) {
                coroutineContext.ensureActive()
            }
            
            val originalPixel = sourcePixels[i]
            val quantizedPixel = ColorSorter.getNearestColor(originalPixel)
            targetPixels[i] = quantizedPixel
            
            val groupIndex = ColorPalette.PALETTE.indexOf(quantizedPixel)
            if (groupIndex in 0..9) {
                buckets[groupIndex].add(i)
            }
        }
        
        return buckets
    }

    private const val CANCELLATION_CHECK_INTERVAL = 4096
}
