package com.example.apexphotolab.android.android_logic

import com.example.apexphotolab.android.android_logic.app.AppState

/**
 * Job: Project Folder Handler.
 * Responsibility: Updates the application state when a project directory is confirmed as set.
 */
class ProjectFolderHandler(private val appState: AppState) {

    fun confirmProjectDirSet() {
        appState.hasValidProjectDir.value = true
    }
}
