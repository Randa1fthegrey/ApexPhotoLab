package com.example.apexphotolab.android.android_logic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/**
 * Job: Application Entry Point.
 * Responsibility: Orchestrates the initialization of state and engines, and provides the top-level UI shell.
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
