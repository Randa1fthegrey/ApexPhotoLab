package com.example.apexphotolab.android.android_logic.app

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.example.apexphotolab.android.android_keys.ProjectDirValidator
import com.example.apexphotolab.android.android_keys.Screen
import com.example.apexphotolab.android.android_keys.SettingsPersistence

/**
 * Job: In-memory state management.
 * Holds the source of truth for the UI's current state.
 */
@Composable
fun rememberAppState(): AppState {
    val context = LocalContext.current
    val useDarkTheme = rememberSaveable { mutableStateOf(SettingsPersistence.getTheme(context)) }
    val currentScreen = rememberSaveable { mutableStateOf<Screen>(Screen.Welcome) }
    val hasValidProjectDir = remember { mutableStateOf(ProjectDirValidator.isValid(context, SettingsPersistence.getProjectDir(context))) }

    return remember(useDarkTheme, currentScreen, hasValidProjectDir) {
        AppState(useDarkTheme, currentScreen, hasValidProjectDir)
    }
}

class AppState(
    val useDarkTheme: MutableState<Boolean?>,
    val currentScreen: MutableState<Screen>,
    val hasValidProjectDir: MutableState<Boolean>
)
