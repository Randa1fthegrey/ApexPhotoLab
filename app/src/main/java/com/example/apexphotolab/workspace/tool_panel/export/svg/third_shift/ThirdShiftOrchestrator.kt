package com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift

import android.graphics.Bitmap
import android.graphics.Point

/**
 * A single-responsibility orchestrator for the Third Shift (Path Coloring).
 */
object ThirdShiftOrchestrator {

    /**
     * Takes a list of closed paths and performs the entire color analysis pipeline.
     * @param closedPaths The list of paths from the Second Shift.
     * @param quantizedImage The color-quantized image from the First Shift.
     * @return A list of ARGB color integers, one for each path.
     */
    suspend fun run(closedPaths: List<List<Point>>, quantizedImage: Bitmap): List<Int> {
        return ColoringDispatcher.resolveColorsInParallel(closedPaths, quantizedImage)
    }
}