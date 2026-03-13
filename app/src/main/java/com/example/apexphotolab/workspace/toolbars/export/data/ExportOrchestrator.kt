package com.example.apexphotolab.workspace.toolbars.export.data

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.IOException

/**
 * The "Foreman" of the export process.
 * Handles all file system interactions for the transactional export workflow.
 */
object ExportOrchestrator {

    /**
     * Creates a temporary file in the app's private cache directory.
     * This location is always writable without special permissions.
     * @return The [java.io.File] object representing the temporary file.
     */
    fun createTempFile(context: Context, prefix: String, extension: String): File {
        return File.createTempFile(prefix, ".$extension", context.cacheDir)
    }

    /**
     * Moves the content of a successfully created temporary file to its final,
     * user-specified destination directory.
     *
     * @return The [android.net.Uri] of the final, successfully created file.
     * @throws java.io.IOException if any file operation fails.
     */
    @Throws(IOException::class)
    fun finalizeExport(
        context: Context,
        tempFile: File,
        finalDirectoryUri: Uri,
        finalFileName: String,
        mimeType: String
    ): Uri {
        val directory = DocumentFile.fromTreeUri(context, finalDirectoryUri)
            ?: throw IOException("Could not access selected directory.")

        if (!directory.canWrite()) {
            throw IOException("Cannot write to the selected directory.")
        }

        // Overwrite if a file with the same name exists.
        directory.findFile(finalFileName)?.delete()

        val finalDocFile = directory.createFile(mimeType, finalFileName)
            ?: throw IOException("Could not create the final export file.")

        context.contentResolver.openOutputStream(finalDocFile.uri)?.use { outputStream ->
            tempFile.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        } ?: throw IOException("Failed to open output stream for final URI: ${finalDocFile.uri}")

        return finalDocFile.uri
    }
}