package com.example.apexphotolab.working_project.tool_panel.export.data

import android.content.Context
import android.net.Uri
import android.util.Log

/**
 * A manager with the single job of cleaning up export artifacts.
 */
object ExportCleanupManager {

    /**
     * Deletes a file at the given URI, logging the outcome.
     * This is used to clean up partial files after a cancelled or failed export.
     */
    fun cleanup(context: Context, uri: Uri?) {
        uri ?: return // Do nothing if the URI is null

        try {
            val rowsDeleted = context.contentResolver.delete(uri, null, null)
            if (rowsDeleted > 0) {
                Log.d("ExportCleanupManager", "Successfully deleted partial file: $uri")
            } else {
                // This can happen if the file was never created, which is not an error.
                Log.d("ExportCleanupManager", "Cleanup requested, but file did not exist or could not be deleted: $uri")
            }
        } catch (e: Exception) {
            Log.e("ExportCleanupManager", "Error while trying to delete partial file: $uri", e)
        }
    }
}