package com.example.apexphotolab.workspace.tool_panel.export.svg._temp_tools

import android.util.Log

/**
 * A dedicated diagnostic tool to count the final shapes produced by the engine.
 * This tells us exactly how many individual SVG <path> elements the machine sees.
 */
object ShapeCounter {
    private const val TAG = "SVG_SHAPE_COUNT"

    fun log(svg: String) {
        val pathCount = "<path".toRegex().findAll(svg).count()
        val gradientCount = "<linearGradient".toRegex().findAll(svg).count()
        
        Log.d(TAG, "------------------------------------------")
        Log.d(TAG, "FINAL MACHINE VISION REPORT:")
        Log.d(TAG, "Total Shapes (Paths): $pathCount")
        Log.d(TAG, "Total Gradients: $gradientCount")
        Log.d(TAG, "Total Elements: ${pathCount + gradientCount}")
        Log.d(TAG, "------------------------------------------")
    }
}
