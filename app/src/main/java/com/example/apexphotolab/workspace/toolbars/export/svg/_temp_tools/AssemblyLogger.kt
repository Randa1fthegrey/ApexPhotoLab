package com.example.apexphotolab.workspace.toolbars.export.svg._temp_tools

import android.util.Log

/**
 * A temporary diagnostic tool for logging the inputs and outputs of the Assembly Shift.
 */
object AssemblyLogger {

    private const val TAG = "SVG_ASSEMBLY_DIAGNOSTICS"

    /**
     * Logs the number of raw fragments being handed off from the Third Shift.
     */
    fun logHandoff(pathCount: Int, colorCount: Int) {
        Log.d(TAG, "--- Assembly Shift Handoff ---")
        Log.d(TAG, "Received $pathCount path fragments and $colorCount corresponding colors.")
        if (pathCount != colorCount) {
            Log.e(TAG, "CRITICAL ERROR: Mismatch between path fragments and colors!")
        }
    }

    /**
     * Logs the result of the grouping phase.
     */
    fun logGroupingResult(groupCount: Int) {
        Log.d(TAG, "\n--- Grouping Phase Complete ---")
        Log.d(TAG, "Sorted $groupCount color families (chunks).")
    }

    /**
     * Logs the start of the building phase for a single shape.
     */
    fun logShapeBuildStart(groupIndex: Int, fragmentCount: Int) {
        Log.d(TAG, "\n--- Building Shape #$groupIndex ---")
        Log.d(TAG, "Handing $fragmentCount fragments to the ShapeBuilder.")
    }

    /**
     * Logs the result of the building phase for a single shape.
     */
    fun logShapeBuildResult(groupIndex: Int, finalPathSize: Int) {
        Log.d(TAG, "ShapeBuilder returned a final path with $finalPathSize points.")
        if (finalPathSize < 10) { // Arbitrary small number to indicate a likely problem
            Log.w(TAG, "WARNING: Final path for shape #$groupIndex is very small. The shape may be incomplete.")
        }
    }
}
