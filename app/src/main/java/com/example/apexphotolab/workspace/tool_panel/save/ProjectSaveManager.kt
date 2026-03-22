package com.example.apexphotolab.workspace.tool_panel.save

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.workspace.tool_panel.layers.Layer

/**
 * A manager dedicated to the single job of saving the project's data.
 */
object ProjectSaveManager {

    suspend fun saveProject(context: Context, projectDirUri: Uri, layers: List<Layer>, title: String) {
        val projectDir = DocumentFile.fromTreeUri(context, projectDirUri)
        projectDir?.let {
            ProjectManager.saveProject(context, it, layers, title)
        }
    }
}