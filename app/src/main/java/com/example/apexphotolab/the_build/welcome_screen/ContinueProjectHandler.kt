package com.example.apexphotolab.the_build.welcome_screen

import com.example.apexphotolab.android.android_logic.app.AppState
import com.example.apexphotolab.android.android_keys.Screen

/**
 * Job: Continue Project Handler.
 * Responsibility: Initiates the project continuation workflow by opening the explorer.
 */
class ContinueProjectHandler(private val appState: AppState) {
    fun execute() {
        appState.currentScreen.value = Screen.ProjectFileExplorer(isDeleteMode = false)
    }
}
