package com.example.apexphotolab.android.android_logic.app

import androidx.compose.runtime.Composable

/**
 * Job: Application Settings Handler.
 * Responsibility: Synchronizes the AppState with persisted user settings and preferences.
 */
object AppSettingsHandler {

    @Composable
    fun Sync(appState: AppState) {
        AppSettingsSynchronizer.Sync(appState)
    }
}