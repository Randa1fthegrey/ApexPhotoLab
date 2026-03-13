package com.example.apexphotolab.workspace.toolbars.export.svg._temp_tools

import android.util.Log

/**
 * A temporary diagnostic tool for logging the SVG generation process.
 */
object ProcessLogger {

    private const val TAG = "SVG_DIAGNOSTICS" // As commanded by the architect

    /**
     * Logs a message for a specific stage of the process.
     */
    fun log(stage: String, message: String) {
        Log.d(TAG, "[$stage] :: $message")
    }
}
