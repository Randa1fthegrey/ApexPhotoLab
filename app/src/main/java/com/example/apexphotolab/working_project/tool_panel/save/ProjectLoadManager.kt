package com.example.apexphotolab.working_project.tool_panel.save

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.working_project.tool_panel.layers.Layer
import java.io.File

/**
 * A data class to hold the results of a successful project load.
 */
data class ProjectLoadResult(val projectName: String, val layers: List<Layer>)

/**
 * A manager dedicated to the single job of loading a project's data from disk.
 */
object ProjectLoadManager {

    suspend fun loadProject(context: Context, projectDirUri: Uri): ProjectLoadResult? {
        val projectDir = if (projectDirUri.scheme == "content") {
            DocumentFile.fromTreeUri(context, projectDirUri)
        } else {
            val file = File(projectDirUri.path!!)
            DocumentFile.fromFile(file)
        }

        return projectDir?.let {
            val projectName = it.name ?: ""
            val layers = ProjectManager.loadLayers(context, it)
            ProjectLoadResult(projectName, layers)
        }
    }
}
