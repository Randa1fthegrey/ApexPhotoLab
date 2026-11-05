package com.example.apexphotolab.the_build.welcome_screen.continue_project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Job: Compose UI State Container.
 * Responsibility: Holds the current loading state and categorized project lists for UI rendering,
 * initialized with in-memory cached categories for frame-1 instant rendering.
 */
class ProjectListState {
    var isLoading by mutableStateOf(ProjectCache.cachedCategories == null)
        private set

    var categories by mutableStateOf(ProjectCache.cachedCategories ?: ProjectFolderClassifier.Categories())
        private set

    fun markLoading(loading: Boolean) {
        if (ProjectCache.cachedCategories == null) {
            isLoading = loading
        }
    }

    fun updateCategories(newCategories: ProjectFolderClassifier.Categories) {
        ProjectCache.update(newCategories)
        categories = newCategories
        isLoading = false
    }
}
