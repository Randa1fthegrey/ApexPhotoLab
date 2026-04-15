package com.example.apexphotolab.working_project

import android.net.Uri
import androidx.compose.runtime.*

/**
 * Job: UI Flow Worker.
 * Pure worker that stores transient UI visibility for panels and dialogs.
 */
class PanelManager {
    // --- PANEL VISIBILITY ---
    var showLayersPanel by mutableStateOf(false)
    var showExportScreen by mutableStateOf(false)
    var showFilterPanel by mutableStateOf(false)
    var showHistoryPanel by mutableStateOf(false)
    var showResetDialog by mutableStateOf(false)
    var showSaveConfirmDialog by mutableStateOf(false)
    var showLayerNameDialog by mutableStateOf<Uri?>(null)
    var showSnapshotNameDialog by mutableStateOf(false)

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
