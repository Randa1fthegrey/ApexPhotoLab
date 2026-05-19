package com.example.apexphotolab.the_build.welcome_screen.new_project

import android.content.Context
import android.net.Uri
import com.example.apexphotolab.the_build.working_project.managers.project.ProjectManager
import com.example.apexphotolab.the_build.working_project.util.bitmap.BitmapLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Job: New Project Handler.
 * Responsibility: Orchestrates the creation of a new project from an image URI.
 */
class NewProjectHandler(
    private val context: Context,
    private val scope: CoroutineScope,
    private val onProjectCreated: (Uri) -> Unit
) {
    fun execute(imageUri: Uri, projectName: String) {
        scope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                BitmapLoader.decodeCorrectedBitmap(context, imageUri)
            }
            val newProjectDir = bitmap?.let { bm ->
                ProjectManager.createProject(context, projectName, bm)
            }
            withContext(Dispatchers.Main) {
                newProjectDir?.let { dir -> onProjectCreated(dir.uri) }
            }
        }
    }
}
