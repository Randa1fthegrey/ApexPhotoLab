package com.example.apexphotolab.the_build.welcome_screen.delete_project

import android.content.Context
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Job: Logic/Side-Effect Execution.
 * Handles the deletion of project directories and provides user feedback.
 */
object ProjectDeleter {
    suspend fun delete(context: Context, projectDir: DocumentFile): Boolean = withContext(Dispatchers.IO) {
        try {
            if (projectDir.delete()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "${val_util.TOAST_DELETED_PREFIX}${projectDir.name}${val_util.TOAST_DELETED_SUFFIX}", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, val_util.TOAST_ERROR, Toast.LENGTH_SHORT).show()
                }
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error deleting project.", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }
}
