package com.example.apexphotolab.working_project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ColorFilter

/**
 * Job: State Ownership (Filters).
 * A pure data container for workspace-wide visual filters.
 */
class WorkspaceFilterModel {
    var colorFilter: ColorFilter? by mutableStateOf(null)
}
