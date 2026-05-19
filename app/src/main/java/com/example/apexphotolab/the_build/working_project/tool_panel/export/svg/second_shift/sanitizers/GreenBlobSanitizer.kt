package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.sanitizers

import android.graphics.Point

/**
 * Job: Green Blob Sanitizer.
 * Responsibility: Filtering out path artifacts from the Green color group after tracing.
 */
object GreenBlobSanitizer {

    fun sanitizePaths(paths: List<List<Point>>): List<List<Point>> {
        return paths.filter { it.size > 2 }
    }
}
