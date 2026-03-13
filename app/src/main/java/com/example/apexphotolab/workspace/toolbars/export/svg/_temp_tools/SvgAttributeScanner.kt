package com.example.apexphotolab.workspace.toolbars.export.svg._temp_tools

import android.util.Log

/**
 * A diagnostic tool to scan the final SVG string and report on its attributes.
 * This helps us understand what is actually being drawn.
 */
object SvgAttributeScanner {

    private const val TAG = "SVG_SCANNER"

    fun scan(svgContent: String) {
        Log.d(TAG, "--- Starting SVG Scan ---")

        val pathTags = svgContent.split("<path").drop(1)
        val totalPaths = pathTags.size

        if (totalPaths == 0) {
            Log.d(TAG, "Scan complete: No <path> elements found in the SVG.")
            return
        }

        var solidFills = 0
        var gradientFills = 0
        var noFills = 0

        pathTags.forEach { pathTag ->
            val content = pathTag.substringBefore("/>")
            
            when {
                content.contains("fill=\"url(#") -> gradientFills++
                content.contains("fill=\"#") -> solidFills++
                else -> noFills++
            }
        }
        
        Log.d(TAG, "Scan Complete. Here's the report:")
        Log.d(TAG, "------------------------------------")
        Log.d(TAG, "Total <path> elements found: $totalPaths")
        Log.d(TAG, "Paths with SOLID color fills: $solidFills")
        Log.d(TAG, "Paths with GRADIENT fills: $gradientFills")
        Log.d(TAG, "Paths with NO fill attribute specified: $noFills")
        Log.d(TAG, "------------------------------------")

        if (noFills > 0) {
            Log.w(TAG, "WARNING: $noFills paths do not have a fill attribute. These paths are likely being discarded or rendered incorrectly.")
        }
    }
}
