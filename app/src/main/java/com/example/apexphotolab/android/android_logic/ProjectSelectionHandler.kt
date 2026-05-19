package com.example.apexphotolab.android.android_logic

import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.android.android_logic.app.AppState

/**
 * Job: Project Selection Handler.
 * Responsibility: Resolves the result of a project explorer action, transitioning to the correct screen.
 */
class ProjectSelectionHandler(
    private val appState: AppState,
    private val navHandler: NavigationHandler
) {

    fun execute(file: DocumentFile, isDeleteMode: Boolean) {
        if (isDeleteMode) {
            navHandler.navigateToWelcome()
        } else {
            navHandler.navigateToEditor(file.uri)
        }
    }
}
