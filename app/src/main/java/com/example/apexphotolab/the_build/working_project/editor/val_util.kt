package com.example.apexphotolab.the_build.working_project.editor

import androidx.compose.ui.unit.dp

/**
 * Editor Value Utility.
 * Responsibility: Centralizing strings, layout sizes, and identifiers for the editor workspace.
 */
object val_util {

    // ==========================================
    // LAYER IDENTIFIERS
    // ==========================================

    val LAYER_BASE_ID = "base" // *
    val LAYER_BASE_FALLBACK_TITLE = "Background" // *

    // ==========================================
    // UI LAYOUT
    // ==========================================

    val DRAWER_WIDTH = 320.dp

    // ==========================================
    // NOTIFICATIONS
    // ==========================================

    val TOAST_EXPORT_FINISHED = "Export Finished"
    val TOAST_EXPORT_FAILED = "Export Failed"
    val TOAST_LAYER_ADD_FAILED = "Failed to add layer"
    val TOAST_SNAPSHOT_SAVED = "Snapshot Saved"
    val TOAST_ROLLBACK_FAILED = "Rollback Failed"
    val TOAST_PROJECT_RESTORED = "Project Restored"
    val TOAST_PROJECT_SAVED = "Saved project!"
    val TOAST_SAVE_FAILED = "Save Failed"
}
