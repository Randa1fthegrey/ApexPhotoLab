package com.example.apexphotolab.workspace.tool_panel.export.svg._temp_tools

import android.graphics.Color
import com.example.apexphotolab.workspace.tool_panel.export.svg.first_shift.color.ColorPalette

/**
 * THE TOGGLE SWITCH
 * Flip IS_XRAY_ENABLED to true to see the "Developer's X-ray" view:
 * - Fills are disabled (fill="none")
 * - Every corner/point where the tracer makes a turn is numbered.
 */
object XRayControl {
    const val IS_XRAY_ENABLED = false
    
    // Global counter to ensure every point across the entire image has a unique ID
    var globalPointCounter = 1

    fun resetCounter() {
        globalPointCounter = 1
    }

    /**
     * TARGETED X-RAY: Only labels paths within the Magenta color range.
     * Keeps the view clean while we hunt specific splinters.
     */
    fun isTargetColor(color: Int): Boolean {
        // Just checking if the color falls within the Magenta Ramp indices
        // Using a simple RGB check as a fallback if palette indexing is complex
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        // Magenta is high Red, low Green, high Blue
        return r > 200 && g < 100 && b > 200
    }
}
