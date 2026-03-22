package com.example.apexphotolab.workspace.tool_panel.export.svg.first_shift.managers

import com.example.apexphotolab.workspace.tool_panel.export.svg.first_shift.ColorSorter
import com.example.apexphotolab.workspace.tool_panel.export.svg.first_shift.FirstShiftWorker
import kotlinx.coroutines.ensureActive
import java.nio.ByteBuffer
import kotlin.coroutines.coroutineContext

/**
 * Manager #8 for the First Shift.
 */
object Manager8 :
    FirstShiftWorker {

    override val id = 8
    private const val CANCELLATION_CHECK_INTERVAL = 4096

    override suspend fun processSlice(
        sourcePixels: IntArray,
        targetPixels: IntArray,
        slice: IntRange,
        vramSlot: ByteBuffer
    ) {
        val sliceCount = slice.count()
        if (vramSlot.capacity() < sliceCount * 4) {
            for (i in slice) {
                coroutineContext.ensureActive()
                targetPixels[i] = ColorSorter.getNearestColor(sourcePixels[i])
            }
            return
        }

        vramSlot.clear()
        val intVram = vramSlot.asIntBuffer()

        for (i in 0 until sliceCount) {
            intVram.put(i, sourcePixels[slice.first + i])
        }

        for (i in 0 until sliceCount) {
            if (i % CANCELLATION_CHECK_INTERVAL == 0) {
                coroutineContext.ensureActive()
            }
            val originalPixel = intVram.get(i)
            val quantizedPixel = ColorSorter.getNearestColor(originalPixel)
            intVram.put(i, quantizedPixel)
        }

        for (i in 0 until sliceCount) {
            targetPixels[slice.first + i] = intVram.get(i)
        }

        coroutineContext.ensureActive()
    }
}
