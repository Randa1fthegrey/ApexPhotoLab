package com.example.apexphotolab.the_build.working_project.tool_panel.eraser

/**
 * Job: UI Metadata Extension for EraserMode.
 * Maps the logic-only enum to UI display labels.
 */
val EraserMode.label: String
    get() = when (this) {
        EraserMode.FREEFORM -> "Freeform (Touch)"
        EraserMode.PRECISION -> "Precision (Offset)"
    }
