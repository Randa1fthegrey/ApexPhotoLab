package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.sanitizers

import android.graphics.Point

/**
 * Path Sanitizer for the BLACK color group.
 * Its job is to filter out "dust" (tiny artifacts) after the tracing is complete.
 */
object BlackBlobSanitizer {

    /**
     * Inspects the traced paths and removes loose ends or tiny artifacts.
     */
    fun sanitizePaths(paths: List<List<Point>>): List<List<Point>> {
        // Dust Filter: Only keep shapes that have at least 3 points (minimum for a triangle/line)
        return paths.filter { it.size > 2 }
    }
}
