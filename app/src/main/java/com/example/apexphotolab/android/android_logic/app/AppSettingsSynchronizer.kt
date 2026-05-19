package com.example.apexphotolab.android.android_logic.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.apexphotolab.android.android_keys.ProjectDirValidator
import com.example.apexphotolab.android.android_keys.SettingsPersistence

/**
 * Job: Persistence Synchronization.
 * Ensures that changes in the UI state are immediately saved to disk.
 */
object AppSettingsSynchronizer {
    @Composable
    fun Sync(appState: AppState) {
        val context = LocalContext.current

        // Sync Theme changes to disk
        LaunchedEffect(appState.useDarkTheme.value) {
            SettingsPersistence.setTheme(context, appState.useDarkTheme.value)
        }

        // Sync Project Directory validity check
        LaunchedEffect(Unit) {
            appState.hasValidProjectDir.value =
                ProjectDirValidator.isValid(context, SettingsPersistence.getProjectDir(context))
        }
    }
}