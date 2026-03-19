package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.sanitizers

import android.graphics.Point

/**
 * Path Sanitizer for the GREY color group.
 */
object GreyBlobSanitizer {

    fun sanitizePaths(paths: List<List<Point>>): List<List<Point>> {
        return paths.filter { it.size > 2 }
    }
}
