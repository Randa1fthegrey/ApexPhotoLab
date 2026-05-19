package com.example.apexphotolab.android.android_logic.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.apexphotolab.android.android_logic.NavigationHandler
import com.example.apexphotolab.android.android_logic.ProjectFolderHandler
import com.example.apexphotolab.android.android_logic.ProjectSelectionHandler
import com.example.apexphotolab.the_build.ui.theme.ApexPhotoLabTheme

/**
 * Job: UI Root Orchestrator.
 * Responsibility: Manages the top-level Scaffold, Theme, and provides the navigation and logic handlers.
 */
@Composable
fun AppRoot(appState: AppState) {
    // Instantiate specialized handlers for state and navigation transitions
    val navHandler = remember(appState) { NavigationHandler(appState) }
    val folderHandler = remember(appState) { ProjectFolderHandler(appState) }
    val selectionHandler = remember(appState, navHandler) { ProjectSelectionHandler(appState, navHandler) }

    ApexPhotoLabTheme(darkTheme = appState.useDarkTheme.value ?: isSystemInDarkTheme()) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            AppRouter(
                appState = appState,
                navHandler = navHandler,
                folderHandler = folderHandler,
                selectionHandler = selectionHandler,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
