package com.example.apexphotolab.workspace.tool_panel.export.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * A simple, single-responsibility utility for saving text content to a file.
 */
object TextFileSaver {

    /**
     * Writes the given text content to the specified URI.
     * This function will throw an [java.io.IOException] if the file cannot be written.
     */
    suspend fun saveText(context: Context, textContent: String, uri: Uri) =
        withContext(Dispatchers.IO) {
            // The `use` block automatically handles closing the stream.
            // If openOutputStream returns null or if any IO error occurs, an exception will be thrown.
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(textContent.toByteArray())
            } ?: throw IOException("Failed to open output stream for URI: $uri")
        }
}