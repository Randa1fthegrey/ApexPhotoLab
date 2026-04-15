package com.example.apexphotolab.working_project.tool_panel.export.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * A flexible utility for saving bitmaps with specific formats and quality.
 */
object BitmapFileSaver {

    /**
     * Writes the given bitmap to the specified URI.
     * Updated: Supports dynamic formats and quality for file-size targeting.
     */
    suspend fun saveBitmap(
        context: Context, 
        bitmap: Bitmap, 
        uri: Uri,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100
    ) = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            if (!bitmap.compress(format, quality, outputStream)) {
                throw IOException("Failed to compress bitmap for URI: $uri")
            }
        } ?: throw IOException("Failed to open output stream for URI: $uri")
    }
}
