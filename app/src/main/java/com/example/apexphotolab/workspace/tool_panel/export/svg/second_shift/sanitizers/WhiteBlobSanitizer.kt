package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.sanitizers

import android.graphics.Point

/**
 * Path Sanitizer for the WHITE color group.
 */
object WhiteBlobSanitizer {

    fun sanitizePaths(paths: List<List<Point>>): List<List<Point>> {
        return paths.filter { it.size > 2 }
    }
}
