package com.example.apexphotolab.the_build.working_project.managers

import androidx.compose.runtime.*

/**
 * Job: UI Flow Worker.
 * Pure worker that stores transient UI visibility for main workspace panels.
 */
class PanelManager {
    // --- PANEL VISIBILITY ---
    var showLayersPanel by mutableStateOf(false)
    var showExportScreen by mutableStateOf(false)
    var showFilterPanel by mutableStateOf(false)
    var showHistoryPanel by mutableStateOf(false)

    fun clearPanelStates() {
        showLayersPanel = false
        showExportScreen = false
        showFilterPanel = false
        showHistoryPanel = false
    }

    fun openPanel(panel: WorkspacePanel) {
        clearPanelStates()
        when (panel) {
            WorkspacePanel.LAYERS -> showLayersPanel = true
            WorkspacePanel.EXPORT -> showExportScreen = true
            WorkspacePanel.FILTERS -> showFilterPanel = true
            WorkspacePanel.HISTORY -> showHistoryPanel = true
        }
    }
}

enum class WorkspacePanel {
    LAYERS, EXPORT, FILTERS, HISTORY
}
