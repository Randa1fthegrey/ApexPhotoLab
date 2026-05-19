package com.example.apexphotolab.the_build.working_project.managers.export

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.apexphotolab.the_build.working_project.tool_panel.export.ui.ResolutionCategory

/**
 * Job: UI State Worker (Specialist).
 * Responsibility: Holds the persistent configuration for the Export screen.
 */
class ExportConfigurationManager {
    var exportResolution by mutableStateOf("1024 x 1024")
    var exportCategory by mutableStateOf(ResolutionCategory.STANDARD)
    var widescreenIndex by mutableIntStateOf(1)
    var standardIndex by mutableIntStateOf(0)
    var customWidth by mutableStateOf("1024")
    var customHeight by mutableStateOf("1024")
}
