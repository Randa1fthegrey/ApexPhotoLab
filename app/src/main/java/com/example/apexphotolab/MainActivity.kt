package com.example.apexphotolab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.apexphotolab.working_project.app.AppEngineInitializer
import com.example.apexphotolab.working_project.app.AppRouter
import com.example.apexphotolab.working_project.app.AppSettingsSynchronizer
import com.example.apexphotolab.working_project.app.rememberAppState

/**
 * The Orchestrator for the entire application.
 * Promotion Level: Manager of Managers.
 * Responsibility: Coordinates between State, Engine, Sync, and Routing workers.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 1. Manage State (Memory)
            val appState = rememberAppState()

            // 2. Initialize Engine (Hardware)
            AppEngineInitializer.Initialize()

            // 3. Sync Settings (Persistence)
            AppSettingsSynchronizer.Sync(appState)

            // 4. Route UI (Visuals)
            AppRouter.Root(appState)
        }
    }
}
