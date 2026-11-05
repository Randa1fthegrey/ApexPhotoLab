package com.example.apexphotolab.the_build.welcome_screen.continue_project

import android.content.Context
import com.example.apexphotolab.the_build.working_project.managers.project.ProjectFileLister
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Job: Async IO Loader Specialist.
 * Responsibility: Handles background fetching and classification of project files on Dispatchers.IO,
 * updating the memory cache and keeping disk IO completely out of Compose UI state files.
 */
class ProjectListLoader(
    private val context: Context,
    private val scope: CoroutineScope
) {
    fun load(state: ProjectListState) {
        state.markLoading(true)
        scope.launch {
            val rawFiles = ProjectFileLister.getProjectFiles(context)
            val categories = ProjectFolderClassifier.classify(rawFiles)
            withContext(Dispatchers.Main) {
                state.updateCategories(categories)
            }
        }
    }
}
