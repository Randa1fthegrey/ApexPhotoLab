package com.example.apexphotolab.android.android_logic.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.apexphotolab.the_build.welcome_screen.continue_project.ProjectCache
import com.example.apexphotolab.the_build.welcome_screen.continue_project.ProjectFolderClassifier
import com.example.apexphotolab.the_build.working_project.managers.project.ProjectFileLister
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Job: Hardware/Background Service preparation & Cold-Boot Pre-warming.
 * Responsibility: Pre-warms the project directory cache on app startup so Continue Project
 * opens instantly on frame 1 without any initial cold-boot lag.
 */
object AppEngineInitializer {

    @Composable
    fun Initialize() {
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                try {
                    val rawFiles = ProjectFileLister.getProjectFiles(context)
                    val categories = ProjectFolderClassifier.classify(rawFiles)
                    ProjectCache.update(categories)
                } catch (e: Exception) {
                    // Silent background pre-warm
                }
            }
        }
    }
}
