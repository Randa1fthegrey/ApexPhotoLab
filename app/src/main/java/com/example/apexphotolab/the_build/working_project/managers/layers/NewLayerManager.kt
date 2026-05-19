package com.example.apexphotolab.the_build.working_project.managers.layers

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.the_build.working_project.managers.project.ProjectManager
import com.example.apexphotolab.the_build.working_project.util.bitmap.BitmapLoader
import com.example.apexphotolab.the_build.working_project.util.layers.Layer

/**
 * Job: Layer Creation Orchestrator.
 * Responsibility: Decodes image assets and registers them as new Layer entities within the project structure.
 */
object NewLayerManager {

    suspend fun addNewLayer(
        context: Context,
        projectDirUri: Uri,
        imageUri: Uri,
        title: String
    ): Layer? {
        val bitmap = BitmapLoader.decodeCorrectedBitmap(context, imageUri) ?: return null
        
        val projectDir = DocumentFile.fromTreeUri(context, projectDirUri) ?: return null

        return ProjectManager.addImageLayer(context, projectDir, title, bitmap)
    }
}
