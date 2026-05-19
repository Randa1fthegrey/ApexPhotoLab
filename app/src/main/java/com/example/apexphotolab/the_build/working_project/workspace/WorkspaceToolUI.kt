package com.example.apexphotolab.the_build.working_project.workspace

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Job: UI Metadata Extension for WorkspaceTool.
 * Maps the logic-only enum to UI display labels and icons.
 */
val WorkspaceTool.label: String
    get() = when (this) {
        WorkspaceTool.MOVE -> "Move & Transform"
        WorkspaceTool.ERASER -> "Eraser Tool"
        WorkspaceTool.REMOVE_BG -> "Remove Background"
    }

val WorkspaceTool.icon: ImageVector
    get() = when (this) {
        WorkspaceTool.MOVE -> Icons.Default.OpenWith
        WorkspaceTool.ERASER -> Icons.Default.Brush
        WorkspaceTool.REMOVE_BG -> Icons.Default.AutoFixHigh
    }
