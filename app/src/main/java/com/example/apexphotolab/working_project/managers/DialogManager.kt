package com.example.apexphotolab.working_project.managers

import android.net.Uri
import androidx.compose.runtime.*

/**
 * Job: UI Flow Worker.
 * Pure worker that stores transient UI visibility for dialogs/pop-ups.
 */
class DialogManager {
    var showResetDialog by mutableStateOf(false)
    var showSaveConfirmDialog by mutableStateOf(false)
    var showLayerNameDialog by mutableStateOf<Uri?>(null)
    var showSnapshotNameDialog by mutableStateOf(false)
}
