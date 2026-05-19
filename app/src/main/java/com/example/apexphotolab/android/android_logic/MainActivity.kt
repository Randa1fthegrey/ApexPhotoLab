package com.example.apexphotolab.android.android_logic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.apexphotolab.android.android_logic.app.AppEngineHandler
import com.example.apexphotolab.android.android_logic.app.AppRoot
import com.example.apexphotolab.android.android_logic.app.AppSettingsHandler
import com.example.apexphotolab.android.android_logic.app.rememberAppState

/**
 * Job: Application Entry Point.
 * Responsibility: Orchestrates the initialization of state and engines, and launches the top-level UI shell.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            // Memory State (Source of Truth)
            val appState = rememberAppState()

            // Hardware Engine (Preparation)
            AppEngineHandler.Initialize()

            // Persistence Sync (Stability)
            AppSettingsHandler.Sync(appState)

            // UI Root Shell (Visuals)
            AppRoot(appState)
        }
    }
}
