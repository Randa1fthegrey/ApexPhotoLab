package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Job: Text File Saver.
 * Responsibility: Writing text content to a specified URI.
 */
object TextFileSaver {

    suspend fun saveText(context: Context, textContent: String, uri: Uri) =
        withContext(Dispatchers.IO) {
            // The `use` block automatically handles closing the stream.
            // If openOutputStream returns null or if any IO error occurs, an exception will be thrown.
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(textContent.toByteArray())
            } ?: throw IOException("Failed to open output stream for URI: $uri")
        }
}