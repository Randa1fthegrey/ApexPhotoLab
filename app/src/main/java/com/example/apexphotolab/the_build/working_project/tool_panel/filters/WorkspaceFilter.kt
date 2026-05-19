package com.example.apexphotolab.the_build.working_project.tool_panel.filters

import androidx.compose.ui.graphics.ColorFilter

/**
 * Job: Contract (The Shared Language).
 * Defines what a Filter "is" to the rest of the system.
 */
interface WorkspaceFilter {
    val label: String
    val colorFilter: ColorFilter?
}
