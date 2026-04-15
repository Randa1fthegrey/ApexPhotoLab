package com.example.apexphotolab

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.apexphotolab.welcome_screen.WelcomeScreen
import com.example.apexphotolab.welcome_screen.continue_project.ProjectFileExplorer
import com.example.apexphotolab.ui.theme.ApexPhotoLabTheme
import com.example.apexphotolab.working_project.MainEditorScreen

/**
 * Job: UI Root & Navigation Routing.
 * Decides which Screen Composable to draw based on the AppState.
 */
object AppRouter {
    @Composable
    fun Root(appState: AppState) {
        ApexPhotoLabTheme(darkTheme = appState.useDarkTheme.value ?: isSystemInDarkTheme()) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                when (val screen = appState.currentScreen.value) {
                    is Screen.Welcome -> {
                        WelcomeScreen(
                            modifier = Modifier.padding(innerPadding),
                            useDarkTheme = appState.useDarkTheme.value,
                            onThemeChange = { appState.useDarkTheme.value = it },
                            hasValidProjectDir = appState.hasValidProjectDir.value,
                            onContinueProject = {
                                appState.currentScreen.value = Screen.ProjectFileExplorer()
                            },
                            onDeleteProject = {
                                appState.currentScreen.value = Screen.ProjectFileExplorer(isDeleteMode = true)
                            },
                            onStartProject = { projectDirUri ->
                                appState.currentScreen.value = Screen.MainEditor(projectDirUri)
                            },
                            onProjectDirSet = { appState.hasValidProjectDir.value = true }
                        )
                    }
                    is Screen.ProjectFileExplorer -> {
                        BackHandler { appState.currentScreen.value = Screen.Welcome }
                        ProjectFileExplorer(
                            modifier = Modifier.padding(innerPadding),
                            isDeleteMode = screen.isDeleteMode,
                            onProjectSelected = { projectDir ->
                                if (screen.isDeleteMode) {
                                    appState.currentScreen.value = Screen.Welcome
                                } else {
                                    appState.currentScreen.value = Screen.MainEditor(projectDir.uri)
                                }
                            }
                        )
                    }
                    is Screen.MainEditor -> {
                        MainEditorScreen(
                            modifier = Modifier.padding(innerPadding),
                            projectDirUri = screen.projectDirUri,
                            onNavigateBack = { appState.currentScreen.value = Screen.Welcome }
                        )
                    }
                }
            }
        }
    }
}
