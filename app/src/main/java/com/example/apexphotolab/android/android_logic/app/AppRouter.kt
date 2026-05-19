package com.example.apexphotolab.android.android_logic.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.apexphotolab.android.android_keys.Screen
import com.example.apexphotolab.android.android_logic.NavigationHandler
import com.example.apexphotolab.android.android_logic.ProjectFolderHandler
import com.example.apexphotolab.android.android_logic.ProjectSelectionHandler
import com.example.apexphotolab.the_build.welcome_screen.ui.WelcomeScreen
import com.example.apexphotolab.the_build.welcome_screen.ProjectFileExplorer
import com.example.apexphotolab.the_build.working_project.editor.MainEditorScreen

/**
 * Job: Navigation Router.
 * Responsibility: Maps the current Screen state to its corresponding Composable UI.
 */
@Composable
fun AppRouter(
    appState: AppState,
    navHandler: NavigationHandler,
    folderHandler: ProjectFolderHandler,
    selectionHandler: ProjectSelectionHandler,
    modifier: Modifier = Modifier
) {
    when (val screen = appState.currentScreen.value) {
        is Screen.Welcome -> {
            WelcomeScreen(
                modifier = modifier,
                useDarkTheme = appState.useDarkTheme.value,
                onThemeChange = { appState.useDarkTheme.value = it },
                hasValidProjectDir = appState.hasValidProjectDir.value,
                onContinueProject = { navHandler.navigateToProjectExplorer(false) },
                onDeleteProject = { navHandler.navigateToProjectExplorer(true) },
                onStartProject = { navHandler.navigateToEditor(it) },
                onProjectDirSet = { folderHandler.confirmProjectDirSet() }
            )
        }

        is Screen.ProjectFileExplorer -> {
            BackHandler { navHandler.navigateToWelcome() }
            ProjectFileExplorer(
                modifier = modifier,
                isDeleteMode = screen.isDeleteMode,
                onProjectSelected = { selectionHandler.execute(it, screen.isDeleteMode) }
            )
        }

        is Screen.MainEditor -> {
            MainEditorScreen(
                modifier = modifier,
                projectDirUri = screen.projectDirUri,
                onNavigateBack = { navHandler.navigateToWelcome() }
            )
        }
    }
}
