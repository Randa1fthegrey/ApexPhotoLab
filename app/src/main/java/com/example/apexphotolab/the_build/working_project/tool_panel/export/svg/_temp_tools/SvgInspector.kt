package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools

import android.util.Log

/**
 * Diagnostic tool to inspect the final SVG output for glitches and color issues.
 */
object SvgInspector {
    private const val TAG = "SVG"

    fun inspect(svg: String) {
        Log.d(TAG, "[INSPECTOR] Starting SVG analysis...")

        val pathRegex = "<path[^>]*>".toRegex()
        val fillRegex = "fill=\"(#[0-9A-Fa-f]{6}|url\\(#[^\\)]+\\)|none)\"".toRegex()
        val dRegex = "d=\"([^\"]*)\"".toRegex()

        val paths = pathRegex.findAll(svg).toList()
        val gradients = "<linearGradient".toRegex().findAll(svg).count()

        val uniqueColors = mutableSetOf<String>()
        var fillNoneCount = 0
        var emptyPathCount = 0
        var complexPathCount = 0

        paths.forEach { match ->
            val tag = match.value
            
            // Extract Fill
            val fillMatch = fillRegex.find(tag)
            fillMatch?.groupValues?.get(1)?.let { 
                if (it == "none") fillNoneCount++ 
                else uniqueColors.add(it)
            }

            // Extract Data
            val dMatch = dRegex.find(tag)
            val pathData = dMatch?.groupValues?.get(1) ?: ""
            if (pathData.isBlank()) {
                emptyPathCount++
            } else if (pathData.length > 500) {
                complexPathCount++
            }
        }

        Log.d(TAG, "[INSPECTOR] Summary:")
        Log.d(TAG, "  - Total Paths: ${paths.size}")
        Log.d(TAG, "  - Gradients: $gradients")
        Log.d(TAG, "  - Unique Colors: ${uniqueColors.size} -> ${uniqueColors.joinToString(", ")}")
        Log.d(TAG, "  - Unfilled Paths (Strokes only): $fillNoneCount")
        Log.d(TAG, "  - Empty/Glitched Paths: $emptyPathCount")
        Log.d(TAG, "  - High-Detail Paths: $complexPathCount")
        
        if (svg.length > 1000000) {
            Log.w(TAG, "  - WARNING: Large SVG detected (${svg.length / 1024} KB)")
        }
        
        Log.d(TAG, "[INSPECTOR] Analysis complete.")
    }
}
