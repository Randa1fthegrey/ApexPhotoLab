package com.example.apexphotolab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.apexphotolab.ui.theme.ApexPhotoLabTheme
import com.example.apexphotolab.welcome_screen.WelcomeScreen
import com.example.apexphotolab.welcome_screen.continue_project.ProjectFileExplorer
import com.example.apexphotolab.workspace.MainEditorScreen
import com.example.apexphotolab.workspace.toolbars.export.svg.utils.EngineInitializer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            var useDarkTheme by rememberSaveable { mutableStateOf(SettingsManager.getTheme(context)) }
            var currentScreen by rememberSaveable { mutableStateOf<Screen>(Screen.Welcome) }
            var hasValidProjectDir by remember { mutableStateOf(SettingsManager.isProjectDirValid(context)) }

            // Pre-warm the SVG engine on app startup to prevent deadlocks.
            LaunchedEffect(Unit) {
                EngineInitializer.warmUp()
            }

            LaunchedEffect(useDarkTheme) {
                SettingsManager.setTheme(context, useDarkTheme)
            }

            LaunchedEffect(Unit) {
                hasValidProjectDir = SettingsManager.isProjectDirValid(context)
            }

            ApexPhotoLabTheme(darkTheme = useDarkTheme ?: isSystemInDarkTheme()) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (val screen = currentScreen) {
                        is Screen.Welcome -> {
                            WelcomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                useDarkTheme = useDarkTheme,
                                onThemeChange = { useDarkTheme = it },
                                hasValidProjectDir = hasValidProjectDir,
                                onContinueProject = {
                                    currentScreen = Screen.ProjectFileExplorer()
                                },
                                onDeleteProject = {
                                    currentScreen = Screen.ProjectFileExplorer(isDeleteMode = true)
                                },
                                onStartProject = { projectDirUri ->
                                    currentScreen = Screen.MainEditor(projectDirUri)
                                },
                                onProjectDirSet = { hasValidProjectDir = true }
                            )
                        }
                        is Screen.ProjectFileExplorer -> {
                            BackHandler { currentScreen = Screen.Welcome }
                            ProjectFileExplorer(
                                modifier = Modifier.padding(innerPadding),
                                isDeleteMode = screen.isDeleteMode,
                                onProjectSelected = { projectDir ->
                                    if (screen.isDeleteMode) {
                                        currentScreen = Screen.Welcome
                                    } else {
                                        currentScreen = Screen.MainEditor(projectDir.uri)
                                    }
                                }
                            )
                        }
                        is Screen.MainEditor -> {
                            MainEditorScreen(
                                modifier = Modifier.padding(innerPadding),
                                projectDirUri = screen.projectDirUri,
                                onNavigateBack = { currentScreen = Screen.Welcome }
                            )
                        }
                    }
                }
            }
        }
    }
}
