package com.example.apexphotolab.the_build.welcome_screen

import com.example.apexphotolab.android.android_logic.app.AppState
import com.example.apexphotolab.android.android_keys.Screen
import androidx.documentfile.provider.DocumentFile

/**
 * Job: Project Selection Handler.
 * Responsibility: Decides the next screen after a project is selected in the explorer.
 */
class ProjectSelectionHandler(private val appState: AppState) {
    fun execute(file: DocumentFile, isDeleteMode: Boolean) {
        if (isDeleteMode) {
            appState.currentScreen.value = Screen.Welcome
        } else {
            appState.currentScreen.value = Screen.MainEditor(file.uri)
        }
    }
}
