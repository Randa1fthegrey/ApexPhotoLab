package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.gradient_scouts.GradientScoutOrchestrator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.multicolor_team.CrossGroupStitcher
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.multicolor_team.MulticolorTracerDispatcher

/**
 * Job: Second Shift Orchestrator.
 * Responsibility: Running the full second shift path tracing pipeline using pre-sorted color buckets from the First Shift.
 */
object SecondShiftOrchestrator {

    suspend fun run(
        quantizedImage: Bitmap, 
        originalImage: Bitmap,
        colorGroups: List<List<Int>> // Received from First Shift
    ): Pair<List<List<Point>>, HashSet<Point>> {
        val width = quantizedImage.width
        val height = quantizedImage.height
        val pixels = IntArray(width * height)
        quantizedImage.getPixels(pixels, 0, width, 0, 0, width, height)

        // 1. GRADIENT SCOUTS: Scouting for ramps
        GradientScoutOrchestrator.run(quantizedImage, originalImage)

        // 2. MULTICOLOR TEAM: Claim complex regions first (unified tracing)
        val (multiPaths, claimedIndices) = MulticolorTracerDispatcher.scan(pixels, width, height)

        // 3. PARALLEL TRACING: Process pre-sorted color groups (skipping claimed pixels)
        val (singlePaths, allEdges) = SecondShiftDispatcher.traceInParallel(colorGroups, width, height, pixels, claimedIndices)

        // 4. CONSOLIDATION: Combine both results
        val combinedRawPaths = multiPaths + singlePaths
        
        // 5. STITCHING: Final endpoint bridging for open fragments
        val stitchedPaths = CrossGroupStitcher.stitch(combinedRawPaths)

        return Pair(stitchedPaths, allEdges)
    }
}
