package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.managers

import android.content.Context
import android.graphics.Bitmap
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.util.ImageFlattener
import com.example.apexphotolab.the_build.working_project.util.layers.Layer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Job: Logic Specialist (Process Manager).
 * Responsibility: Managing the lifecycle of flattened bitmaps during export.
 * Ensures that bitmaps are correctly flattened, processed, and recycled.
 */
object BitmapExportManager {

    /**
     * Orchestrates the "Flatten -> Action -> Recycle" lifecycle.
     */
    suspend fun flattenAndProcess(
        context: Context,
        layers: List<Layer>,
        applyGreyscale: Boolean,
        targetWidth: Int,
        targetHeight: Int,
        action: suspend (Bitmap) -> Unit
    ) = withContext(Dispatchers.IO) {
        val flattened = ImageFlattener.flattenLayers(
            context, layers, applyGreyscale, targetWidth, targetHeight
        ) ?: throw IllegalStateException("Export failed: Could not flatten layers.")

        try {
            action(flattened)
        } finally {
            flattened.recycle()
        }
    }
}

