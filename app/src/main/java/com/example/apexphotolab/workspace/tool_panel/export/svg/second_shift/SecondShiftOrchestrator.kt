package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift

import android.graphics.Bitmap
import android.graphics.Point

/**
 * The Master Orchestrator for the Second Shift (Path Tracing).
 * It coordinates the sorting of pixels into color groups and dispatches
 * them to specialized teams for surgical edge tracing.
 */
object SecondShiftOrchestrator {

    /**
     * Takes a quantized image and performs the team-based path tracing pipeline.
     */
    suspend fun run(quantizedImage: Bitmap): Pair<List<List<Point>>, HashSet<Point>> {
        val width = quantizedImage.width
        val height = quantizedImage.height
        val pixels = IntArray(width * height)
        quantizedImage.getPixels(pixels, 0, width, 0, 0, width, height)

        // 1. Sort all pixels into their respective color groups (0-7)
        val colorGroups = ColorGroupSorter.groupPixelIndices(pixels)

        // 2. Dispatch each group to its own "Team" for edge finding and tracing.
        // The dispatcher handles the parallel "Lonely Walkers".
        val (pathFragments, allEdges) = SecondShiftDispatcher.traceInParallel(colorGroups, width, height)

        // 3. Return the clean paths and the master edge blueprint for the Assembly Shift.
        return Pair(pathFragments, allEdges)
    }
}