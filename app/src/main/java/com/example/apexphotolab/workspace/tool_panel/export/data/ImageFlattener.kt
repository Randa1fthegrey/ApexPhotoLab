package com.example.apexphotolab.workspace.tool_panel.export.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.apexphotolab.workspace.tool_panel.layers.Layer

/**
 * A single-responsibility utility for loading and flattening layers.
 * Updated: Supports high-quality image resizing for export.
 */
object ImageFlattener {

    /**
     * Flattens layers and resizes the result to the target dimensions.
     */
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

        var sourceMaxWidth = 0
        var sourceMaxHeight = 0

        val bitmaps = visibleLayers.mapNotNull { layer ->
            try {
                context.contentResolver.openInputStream(layer.imageUri)?.use { inputStream ->
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeStream(inputStream, null, options)
                    if (options.outWidth > sourceMaxWidth) sourceMaxWidth = options.outWidth
                    if (options.outHeight > sourceMaxHeight) sourceMaxHeight = options.outHeight
                    
                    context.contentResolver.openInputStream(layer.imageUri)?.use { fullInputStream ->
                        BitmapFactory.decodeStream(fullInputStream)
                    }
                }
            } catch (e: Exception) {
                null
            }
        }

        if (bitmaps.isEmpty() || sourceMaxWidth == 0 || sourceMaxHeight == 0) return null

        // 1. Draw the flattened image at its original maximum resolution first
        val flattened = BitmapDrawer.draw(bitmaps, sourceMaxWidth, sourceMaxHeight, applyGreyscale) ?: return null

        // 2. Perform the high-quality resize
        return if (sourceMaxWidth == targetWidth && sourceMaxHeight == targetHeight) {
            flattened
        } else {
            val scaled = Bitmap.createScaledBitmap(flattened, targetWidth, targetHeight, true)
            flattened.recycle() // Clean up the original
            scaled
        }
    }
}
