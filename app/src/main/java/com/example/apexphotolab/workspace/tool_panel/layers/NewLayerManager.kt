package com.example.apexphotolab.workspace.tool_panel.layers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.workspace.tool_panel.save.ProjectManager

/**
 * A manager dedicated to the single job of adding a new image layer to a project.
 */
object NewLayerManager {

    /**
     * Creates a new image layer from a given image URI and adds it to the project.
     * @return The newly created Layer object, or null if an error occurred.
     */
    suspend fun addNewLayer(
        context: Context,
        projectDirUri: Uri,
        imageUri: Uri,
        title: String
    ): Layer? {
        val bitmap = try {
            context.contentResolver.openInputStream(imageUri)?.use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        if (bitmap == null) {
            return null
        }

        val projectDir =
            DocumentFile.fromTreeUri(context, projectDirUri)
                ?: return null

        return ProjectManager.addImageLayer(context, projectDir, title, bitmap)
    }
}