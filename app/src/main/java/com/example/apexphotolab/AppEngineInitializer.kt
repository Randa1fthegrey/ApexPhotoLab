package com.example.apexphotolab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.apexphotolab.working_project.tool_panel.export.svg.utils.EngineInitializer

/**
 * Job: Hardware/Background Service preparation.
 * Ensures the SVG engine is ready before the user starts editing.
 */
object AppEngineInitializer {
    @Composable
    fun Initialize() {
        // Pre-warm the SVG engine on app startup to prevent deadlocks.
        LaunchedEffect(Unit) {
            EngineInitializer.warmUp()
        }
    }
}
