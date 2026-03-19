package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.sanitizers

import android.graphics.Point

/**
 * Path Sanitizer for the ALPHA color group.
 */
object AlphaBlobSanitizer {

    fun sanitizePaths(paths: List<List<Point>>): List<List<Point>> {
        // Keep almost everything for transparency, just filter out single-point noise.
        return paths.filter { it.size > 2 }
    }
}
