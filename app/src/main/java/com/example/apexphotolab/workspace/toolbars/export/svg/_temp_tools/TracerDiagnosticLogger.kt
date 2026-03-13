package com.example.apexphotolab.workspace.toolbars.export.svg._temp_tools

import android.util.Log

/**
 * A temporary diagnostic tool for monitoring the Smart Path Tracers.
 * Uses the "SVG" tag for easy logcat filtering.
 */
object TracerDiagnosticLogger {

    private const val TAG = "SVG"

    /**
     * Logs the work received by a specific color team.
     */
    fun logWorkReceived(colorTeam: String, edgeCount: Int, blobCount: Int) {
        Log.d(TAG, "[$colorTeam Team] :: Work Received. Edges: $edgeCount, Valid Pixels: $blobCount")
    }

    /**
     * Logs the final summary of work for a color team.
     */
    fun logWorkSummary(colorTeam: String, pathCount: Int) {
        Log.i(TAG, "[$colorTeam Team] :: Work Complete. Total Paths Found: $pathCount")
    }

    /**
     * Logs the creation of a solid fill shape in the assembly phase.
     */
    fun logSolidFill(colorHex: String, pathDataSnippet: String) {
        Log.d(TAG, "[Assembly] :: Created Solid Fill. Color: $colorHex, Data (start): $pathDataSnippet")
    }
}
