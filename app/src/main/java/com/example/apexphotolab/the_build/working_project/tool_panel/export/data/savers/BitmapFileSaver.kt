package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Job: Bitmap Saver.
 * Responsibility: Writing a bitmap to a specified URI with a given format and quality.
 */
object BitmapFileSaver {

    /**
     * Writes the given bitmap to the specified URI.
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