package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.apexphotolab.the_build.working_project.tool_panel.util.BitmapRegistry
import com.example.apexphotolab.the_build.working_project.util.layers.Layer

/**
 * Job: Image Flattener.
 * Responsibility: Loading and flattening all visible layers into a single resized bitmap.
 */
object ImageFlattener {

    fun flattenLayers(
        context: Context,
        layers: List<Layer>,
        applyGreyscale: Boolean = false,
        targetWidth: Int = 1024,
        targetHeight: Int = 1024
    ): Bitmap? {
        Log.d("ImageFlattener", "Resizing export to $targetWidth x $targetHeight")

        val visibleLayers = layers.filter { it.isVisible }.sortedBy { it.zOrder }
        if (visibleLayers.isEmpty()) return null

        // 1. Calculate the "Source Canvas Size"
        // For simplicity, we'll use the target dimensions as our baseline canvas,
        // or we could calculate the max bounding box. For now, let's use the
        // max dimensions encountered among layers to preserve resolution.
        var sourceMaxWidth = targetWidth
        var sourceMaxHeight = targetHeight

        // Find the "natural" max width/height to avoid resolution loss before the final scale
        visibleLayers.forEach {
            if (it.width > sourceMaxWidth) sourceMaxWidth = it.width
            if (it.height > sourceMaxHeight) sourceMaxHeight = it.height
        }

        val items = visibleLayers.mapNotNull { layer ->
            try {
                // Prioritize the working bitmap from the registry for live edits (Eraser/BG Remover)
                val baseBitmap = BitmapRegistry.getWorkingBitmap(layer.id)
                val bitmapToDraw = if (baseBitmap != null) {
                    // Must copy because BitmapDrawer recycles it
                    baseBitmap.copy(baseBitmap.config ?: Bitmap.Config.ARGB_8888, false)
                } else {
                    context.contentResolver.openInputStream(layer.imageUri)?.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                }

                if (bitmapToDraw != null) {
                    BitmapDrawer.LayeredBitmap(bitmapToDraw, layer)
                } else null
            } catch (e: Exception) {
                null
            }
        }

        if (items.isEmpty()) return null

        // 2. Draw the flattened image at high resolution
        val flattened = BitmapDrawer.draw(items, sourceMaxWidth, sourceMaxHeight, applyGreyscale)

        // 3. Cleanup the temporary bitmaps created for this export
        items.forEach { it.bitmap.recycle() }

        // 4. Perform the final resize to the user's requested export size
        return if (sourceMaxWidth == targetWidth && sourceMaxHeight == targetHeight) {
            flattened
        } else {
            val scaled = Bitmap.createScaledBitmap(flattened, targetWidth, targetHeight, true)
            flattened.recycle()
            scaled
        }
    }
}
