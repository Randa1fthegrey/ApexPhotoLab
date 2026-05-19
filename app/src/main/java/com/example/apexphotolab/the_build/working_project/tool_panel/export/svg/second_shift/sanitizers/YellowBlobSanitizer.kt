package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.sanitizers

import android.graphics.Point

/**
 * Job: Yellow Blob Sanitizer.
 * Responsibility: Filtering out path artifacts from the Yellow color group after tracing.
 */
object YellowBlobSanitizer {

    fun sanitizePaths(paths: List<List<Point>>): List<List<Point>> {
        return paths.filter { it.size > 2 }
    }
}
