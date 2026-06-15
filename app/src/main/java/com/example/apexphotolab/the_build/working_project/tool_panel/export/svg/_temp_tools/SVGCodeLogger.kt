package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools

import android.util.Log

/**
 * Job: SVG Code Logger.
 * Responsibility: Printing the entire generated SVG code to Logcat for inspection.
 */
object SVGCodeLogger {
    private const val TAG = "SVG_CODE"
    private const val MAX_LOG_LENGTH = 3500

    fun log(svg: String) {
        // Using ERROR level to ensure visibility and bypass some filters
        Log.e(TAG, "################ START SVG CODE ################")
        
        if (svg.length <= MAX_LOG_LENGTH) {
            Log.e(TAG, svg)
        } else {
            var i = 0
            while (i < svg.length) {
                val end = if (i + MAX_LOG_LENGTH < svg.length) i + MAX_LOG_LENGTH else svg.length
                Log.e(TAG, svg.substring(i, end))
                i = end
            }
        }
        
        Log.e(TAG, "################# END SVG CODE #################")
    }
}
