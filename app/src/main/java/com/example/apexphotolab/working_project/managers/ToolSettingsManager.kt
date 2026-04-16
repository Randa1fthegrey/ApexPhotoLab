package com.example.apexphotolab.working_project.managers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.apexphotolab.working_project.workspace.WorkspaceTool
import com.example.apexphotolab.working_project.tool_panel.eraser.EraserMode

/**
 * Job: Interaction Worker.
 * Tracks current tool selection, parameters, and layer focus.
 */
class ToolSettingsManager {
    var selectedLayerId by mutableStateOf("base")
    var activeTool by mutableStateOf(WorkspaceTool.MOVE)
    var brushSize by mutableFloatStateOf(20f)
    var eraserMode by mutableStateOf(EraserMode.FREEFORM)
}