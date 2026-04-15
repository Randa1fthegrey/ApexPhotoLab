package com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.gradient_scouts.GradientScoutOrchestrator

/**
 * The Master Orchestrator for the Second Shift (Path Tracing).
 * High-Fidelity Update: Gradient Scouts run first to map out smears before tracing.
 */
object SecondShiftOrchestrator {

    /**
     * Takes a quantized image and performs the team-based path tracing pipeline.
     */
    suspend fun run(quantizedImage: Bitmap, originalImage: Bitmap): Pair<List<List<Point>>, HashSet<Point>> {
        val width = quantizedImage.width
        val height = quantizedImage.height
        val pixels = IntArray(width * height)
        quantizedImage.getPixels(pixels, 0, width, 0, 0, width, height)

        // 1. TEAM 0: GRADIENT SCOUTS
        // They go first, scanning BOTH images for color smears and populating the Intelligence Agency.
        GradientScoutOrchestrator.run(quantizedImage, originalImage)

        // 2. Initial Sorting
        val colorGroups = ColorGroupSorter.groupPixelIndices(pixels, width, height)

        // 3. Dispatch all groups to specialized tracer teams.
        // Tracers will now have access to the GradientIntelligenceAgency reports.
        val (allPaths, allEdges) = SecondShiftDispatcher.traceInParallel(colorGroups, width, height, pixels)

        return Pair(allPaths, allEdges)
    }
}
