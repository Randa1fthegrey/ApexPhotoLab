package com.example.apexphotolab.the_build.working_project.managers.export

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.util.ExportType
import kotlinx.coroutines.Job

/**
 * Job: Process/Life-cycle Worker (Specialist).
 * Responsibility: Manages the active state of an export operation.
 */
class ExportLifecycleManager {
    var pendingExportType by mutableStateOf<ExportType?>(null)
    var showExportProgress by mutableStateOf(false)
    var exportProgress by mutableFloatStateOf(0f)
    var exportJob by mutableStateOf<Job?>(null)
}