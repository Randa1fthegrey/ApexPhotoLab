package com.example.apexphotolab.workspace.tool_panel.export.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * A simple, single-responsibility utility for saving a bitmap to a file.
 */
object BitmapFileSaver {

    /**
     * Writes the given bitmap to the specified URI as a PNG.
     * This function will throw an [java.io.IOException] if the file cannot be written.
     */
    suspend fun saveBitmap(context: Context, bitmap: Bitmap, uri: Uri) =
        withContext(Dispatchers.IO) {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                    throw IOException("Failed to compress bitmap for URI: $uri")
                }
            } ?: throw IOException("Failed to open output stream for URI: $uri")
        }
}