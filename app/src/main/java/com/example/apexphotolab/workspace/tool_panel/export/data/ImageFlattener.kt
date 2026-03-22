package com.example.apexphotolab.workspace.tool_panel.export.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.apexphotolab.workspace.tool_panel.layers.Layer

/**
 * A single-responsibility utility for loading a list of layers from disk into bitmaps.
 */
object ImageFlattener {

    /**
     * Takes a list of layers, loads them into Bitmaps, and then flattens them into a single Bitmap.
     */
    fun flattenLayers(context: Context, layers: List<Layer>, applyGreyscale: Boolean = false): Bitmap? {
        Log.d("ImageFlattener", "Received ${layers.size} total layers for flattening.")
        
        val visibleLayers = layers.filter { it.isVisible }.sortedBy { it.zOrder }
        Log.d("ImageFlattener", "Found ${visibleLayers.size} visible layers.")

        if (visibleLayers.isEmpty()) {
            Log.e("ImageFlattener", "Flattening failed: No visible layers found in the list.")
            return null
        }

        var maxWidth = 0
        var maxHeight = 0

        val bitmaps = visibleLayers.mapNotNull { layer ->
            try {
                Log.d("ImageFlattener", "Loading layer: ${layer.title} (URI: ${layer.imageUri})")
                context.contentResolver.openInputStream(layer.imageUri)?.use { inputStream ->
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeStream(inputStream, null, options)
                    if (options.outWidth > maxWidth) maxWidth = options.outWidth
                    if (options.outHeight > maxHeight) maxHeight = options.outHeight
                    
                    context.contentResolver.openInputStream(layer.imageUri)?.use { fullInputStream ->
                        BitmapFactory.decodeStream(fullInputStream)
                    }
                }
            } catch (e: Exception) {
                Log.e("ImageFlattener", "Failed to load layer ${layer.title}: ${e.message}")
                e.printStackTrace()
                null
            }
        }

        if (bitmaps.isEmpty() || maxWidth == 0 || maxHeight == 0) {
            Log.e("ImageFlattener", "Flattening failed: Bitmaps list is empty after loading.")
            return null
        }

        Log.d("ImageFlattener", "Successfully loaded ${bitmaps.size} bitmaps. Drawing final image...")
        return BitmapDrawer.draw(bitmaps, maxWidth, maxHeight, applyGreyscale)
    }
}