package com.example.apexphotolab.working_project

import androidx.compose.runtime.*
import com.example.apexphotolab.working_project.tool_panel.eraser_bkgdremover.EraserMode

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
