package com.example.apexphotolab.android.android_logic

import android.net.Uri
import com.example.apexphotolab.android.android_keys.Screen
import com.example.apexphotolab.android.android_logic.app.AppState

/**
 * Job: Application Navigation Handler.
 * Responsibility: Executes pure state transitions between main application screens.
 */
class NavigationHandler(private val appState: AppState) {

    fun navigateToProjectExplorer(isDeleteMode: Boolean = false) {
        appState.currentScreen.value = Screen.ProjectFileExplorer(isDeleteMode)
    }

    fun navigateToEditor(uri: Uri) {
        appState.currentScreen.value = Screen.MainEditor(uri)
    }

    fun navigateToWelcome() {
        appState.currentScreen.value = Screen.Welcome
    }
}
