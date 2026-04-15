package com.example.apexphotolab.welcome_screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.apexphotolab.SettingsPersistence
import com.example.apexphotolab.working_project.tool_panel.save.ProjectManager
import com.example.apexphotolab.working_project.util.BitmapLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Job: Logic/Side-Effect Execution.
 * Orchestrates the "Start Project" and "Set Directory" actions.
 */
class WelcomeActionHandlers(
    private val context: Context,
    private val scope: CoroutineScope,
    private val onStartProject: (Uri) -> Unit,
    private val onProjectDirSet: () -> Unit
) {
    fun handleImageResult(uri: Uri?, projectName: String) {
        uri?.let {
            scope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    BitmapLoader.decodeCorrectedBitmap(context, it)
                }
                val newProjectDir = bitmap?.let { bm ->
                    ProjectManager.createProject(context, projectName, bm)
                }
                withContext(Dispatchers.Main) {
                    newProjectDir?.let { dir -> onStartProject(dir.uri) }
                }
            }
        }
    }

    fun handleProjectDirResult(uri: Uri?) {
        uri?.let {
            SettingsPersistence.setProjectDir(context, it)
            onProjectDirSet()
            Toast.makeText(context, "Custom project folder set!", Toast.LENGTH_SHORT).show()
        }
    }
}
