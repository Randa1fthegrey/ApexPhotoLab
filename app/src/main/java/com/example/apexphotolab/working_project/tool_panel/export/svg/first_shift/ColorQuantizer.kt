package com.example.apexphotolab.working_project.tool_panel.export.svg.first_shift

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The "CEO" of the Color Quantizer department.
 * Prepares pixel arrays and delegates the slicing and dispatching.
 */
object ColorQuantizer {

    suspend fun quantize(image: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = image.width
        val height = image.height
        val sourcePixels = IntArray(width * height)
        image.getPixels(sourcePixels, 0, width, 0, 0, width, height)

        val targetPixels = IntArray(width * height)

        val workSlices = FirstShiftSlicer.createSlices(sourcePixels.size)

        WorkDispatcher.dispatch(workSlices, sourcePixels, targetPixels)

        return@withContext Bitmap.createBitmap(targetPixels, width, height, Bitmap.Config.ARGB_8888)
    }
}