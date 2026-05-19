package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color

import android.graphics.Bitmap
import android.graphics.Color
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.FirstShiftSlicer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.WorkDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Job: Color Quantization Orchestrator.
 * Responsibility: Preparing pixel arrays and coordinating the slicing and dispatching pipeline, returning both the bitmap and sorted color buckets.
 */
object ColorQuantizer {

    suspend fun quantize(image: Bitmap): Pair<Bitmap, List<List<Int>>> = withContext(Dispatchers.Default) {
        val width = image.width
        val height = image.height
        val sourcePixels = IntArray(width * height)
        image.getPixels(sourcePixels, 0, width, 0, 0, width, height)

        val targetPixels = IntArray(width * height)

        val workSlices = FirstShiftSlicer.createSlices(sourcePixels.size)

        // 1. Dispatch the swarm to categorize every pixel
        val unsortedBuckets = WorkDispatcher.dispatch(workSlices, sourcePixels, targetPixels)

        // 2. INTENSITY SORTING: Sort each bucket in descending order of original pixel intensity
        // This provides the "Slope" data for the later shifts.
        val sortedBuckets = unsortedBuckets.map { bucket ->
            bucket.sortedByDescending { idx ->
                val p = sourcePixels[idx]
                Color.red(p) + Color.green(p) + Color.blue(p)
            }
        }

        val bitmap = Bitmap.createBitmap(targetPixels, width, height, Bitmap.Config.ARGB_8888)
        return@withContext Pair(bitmap, sortedBuckets)
    }
}
