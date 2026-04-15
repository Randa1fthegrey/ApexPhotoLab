package com.example.apexphotolab.working_project

import androidx.compose.runtime.*
import com.example.apexphotolab.working_project.tool_panel.export.data.ExportType
import com.example.apexphotolab.working_project.tool_panel.export.ui.ResolutionCategory
import kotlinx.coroutines.Job

/**
 * Job: Export Logic Worker.
 * Manages configuration and execution lifecycle of an export.
 */
class ExportManager {
    // --- CONFIGURATION ---
    var exportResolution by mutableStateOf("1024 x 1024")
    var exportCategory by mutableStateOf(ResolutionCategory.STANDARD)
    var widescreenIndex by mutableIntStateOf(1)
    var standardIndex by mutableIntStateOf(0)
    var customWidth by mutableStateOf("1024")
    var customHeight by mutableStateOf("1024")

    // --- EXECUTION LIFECYCLE ---
    var pendingExportType by mutableStateOf<ExportType?>(null)
    var showExportProgress by mutableStateOf(false)
    var exportProgress by mutableFloatStateOf(0f)
    var exportJob by mutableStateOf<Job?>(null)
}
