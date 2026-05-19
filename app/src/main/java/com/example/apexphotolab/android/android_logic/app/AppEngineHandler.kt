package com.example.apexphotolab.android.android_logic.app

import androidx.compose.runtime.Composable
import com.example.apexphotolab.android.android_logic.app.AppEngineInitializer

/**
 * Job: Application Engine Handler.
 * Responsibility: Initializes low-level engine components and hardware-specific configurations.
 */
object AppEngineHandler {

    @Composable
    fun Initialize() {
        AppEngineInitializer.Initialize()
    }
}