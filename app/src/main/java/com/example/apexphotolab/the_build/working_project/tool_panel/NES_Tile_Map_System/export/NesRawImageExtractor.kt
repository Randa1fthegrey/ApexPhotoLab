package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.apexphotolab.the_build.working_project.tool_panel.util.BitmapRegistry
import com.example.apexphotolab.the_build.working_project.util.layers.Layer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Job: Raw Image Extractor Specialist.
 * Responsibility: Bypasses PhotoLab's standard scaling and flattening pipeline to extract
 * the raw, high-res, untouched Bitmap from the base layer for NES processing.
 */
object NesRawImageExtractor {

    suspend fun extractRawBaseImage(context: Context, layers: List<Layer>): Bitmap? = withContext(Dispatchers.IO) {
        val baseLayer = layers.firstOrNull { it.isVisible } ?: return@withContext null

        try {
            // Check memory registry first for live edits
            val workingBitmap = BitmapRegistry.getWorkingBitmap(baseLayer.id)
            if (workingBitmap != null) {
                return@withContext workingBitmap.copy(workingBitmap.config ?: Bitmap.Config.ARGB_8888, false)
            }

            // Fallback to loading original unscaled file from disk
            context.contentResolver.openInputStream(baseLayer.imageUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            null
        }
    }
}
