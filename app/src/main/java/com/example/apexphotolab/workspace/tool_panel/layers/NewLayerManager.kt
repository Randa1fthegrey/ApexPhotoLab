package com.example.apexphotolab.workspace.tool_panel.layers

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.workspace.tool_panel.save.ProjectManager
import com.example.apexphotolab.workspace.util.BitmapUtils

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
        val bitmap = BitmapUtils.decodeCorrectedBitmap(context, imageUri)

        if (bitmap == null) {
            return null
        }

        val projectDir =
            DocumentFile.fromTreeUri(context, projectDirUri)
                ?: return null

        return ProjectManager.addImageLayer(context, projectDir, title, bitmap)
    }
}