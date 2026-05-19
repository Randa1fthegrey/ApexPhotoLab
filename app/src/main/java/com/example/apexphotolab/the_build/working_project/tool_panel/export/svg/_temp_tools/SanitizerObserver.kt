package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools

import android.graphics.Point
import android.util.Log

/**
 * Diagnostic tool to monitor the efficiency and impact of the Path Sanitizers.
 * Updated: Now monitors SHAPES (paths) instead of pixels.
 */
object SanitizerObserver {
    private const val TAG = "SVG"
    
    private val GROUP_NAMES = listOf(
        "Red", "Green", "Blue", "Yellow", "Cyan", 
        "Magenta", "White", "Alpha", "Black", "Grey"
    )

    /**
     * Reports the raw path count before sanitization.
     */
    fun logPathInput(index: Int, paths: List<List<Point>>) {
        val name = GROUP_NAMES.getOrElse(index) { "Unknown" }
        Log.d(TAG, "[SANITIZER] Team $name: Received ${paths.size} raw shapes")
    }

    /**
     * Reports the remaining path count after sanitization.
     */
    fun logPathOutput(index: Int, paths: List<List<Point>>) {
        val name = GROUP_NAMES.getOrElse(index) { "Unknown" }
        Log.d(TAG, "[SANITIZER] Team $name: Produced ${paths.size} clean shapes")
    }
}
