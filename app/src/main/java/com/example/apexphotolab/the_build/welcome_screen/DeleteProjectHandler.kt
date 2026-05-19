package com.example.apexphotolab.the_build.welcome_screen

import com.example.apexphotolab.android.android_logic.app.AppState
import com.example.apexphotolab.android.android_keys.Screen

/**
 * Job: Delete Project Handler.
 * Responsibility: Initiates the project deletion workflow by opening the explorer in delete mode.
 */
class DeleteProjectHandler(private val appState: AppState) {
    fun execute() {
        appState.currentScreen.value = Screen.ProjectFileExplorer(isDeleteMode = true)
    }
}
