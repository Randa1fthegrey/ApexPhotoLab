package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.sanitizers

import android.graphics.Point

/**
 * Job: Alpha Blob Sanitizer.
 * Responsibility: Filtering out path artifacts from the Alpha color group after tracing.
 */
object AlphaBlobSanitizer {

    fun sanitizePaths(paths: List<List<Point>>): List<List<Point>> {
        return paths.filter { it.size > 2 }
    }
}
