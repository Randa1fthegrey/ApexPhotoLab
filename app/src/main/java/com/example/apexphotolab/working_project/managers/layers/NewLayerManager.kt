package com.example.apexphotolab.working_project.managers.layers

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.working_project.managers.project.ProjectManager
import com.example.apexphotolab.working_project.util.bitmap.BitmapLoader
import com.example.apexphotolab.working_project.util.layers.Layer

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
        val bitmap = BitmapLoader.decodeCorrectedBitmap(context, imageUri)

        if (bitmap == null) {
            return null
        }

        val projectDir =
            DocumentFile.fromTreeUri(context, projectDirUri)
                ?: return null

        return ProjectManager.addImageLayer(context, projectDir, title, bitmap)
    }
}