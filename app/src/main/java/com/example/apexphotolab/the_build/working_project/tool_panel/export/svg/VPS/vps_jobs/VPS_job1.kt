package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.vps_jobs

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.palette.ColorPalette
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.palette.ColorSorter
import kotlinx.coroutines.ensureActive
import java.nio.ByteBuffer
import kotlin.coroutines.coroutineContext

/**
 * Job: VPS Job 1 - Quantization.
 * Responsibility: Processing a pixel slice and sorting pixels into 10 "Absolute Truth" buckets.
 */
object VPS_job1 {

    data class QuantizationData(
        val sourcePixels: IntArray,
        val targetPixels: IntArray,
        val slice: IntRange,
        val vramSlot: ByteBuffer,
        val results: List<MutableList<Int>>
    )

    suspend fun execute(workerId: Int, data: Any?) {
        val qData = data as? QuantizationData ?: return

        for (i in qData.slice) {
            if (i % val_util.CANCELLATION_CHECK_INTERVAL == 0) {
                coroutineContext.ensureActive()
            }

            val originalPixel = qData.sourcePixels[i]
            val quantizedPixel = ColorSorter.getNearestColor(originalPixel)
            qData.targetPixels[i] = quantizedPixel

            val groupIndex = ColorPalette.PALETTE.indexOf(quantizedPixel)
            if (groupIndex in 0..9) {
                qData.results[groupIndex].add(i)
            }
        }
    }
}
