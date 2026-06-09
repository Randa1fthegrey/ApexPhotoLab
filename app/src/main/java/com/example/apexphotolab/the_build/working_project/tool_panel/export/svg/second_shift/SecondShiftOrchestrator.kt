package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.dispatchers.MulticolorTracerDispatcher
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.dispatchers.SecondShiftDispatcher
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure.SecondShiftPixelExtractor
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure.SecondShiftResultMerger
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.scouts.GradientScoutOrchestrator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.stitching.CrossGroupStitcher

/**
 * Job: Second Shift Orchestrator (The Master Manager).
 * Responsibility: Coordinating the specialized scouts, tracers, and stitchers to produce watertight geometry.
 */
object SecondShiftOrchestrator {

    suspend fun run(
        quantizedImage: Bitmap, 
        originalImage: Bitmap,
        colorGroups: List<List<Int>>
    ): Pair<List<List<Point>>, HashSet<Point>> {
        val width = quantizedImage.width
        val height = quantizedImage.height
        
        // 1. DATA PREPARATION
        val pixels = SecondShiftPixelExtractor.extract(quantizedImage)

        // 2. GRADIENT SCOUTS: Pre-scan for ramps
        GradientScoutOrchestrator.run(quantizedImage, originalImage)

        // 3. MULTICOLOR TEAM: Claim complex regions first
        val (multiPaths, claimedIndices) = MulticolorTracerDispatcher.scan(pixels, width, height)

        // 4. STANDARD TEAM: Process sorted color groups (skipping claimed pixels)
        val (singlePaths, allEdges) = SecondShiftDispatcher.traceInParallel(colorGroups, width, height, pixels, claimedIndices)

        // 5. DATA CONSOLIDATION
        val combinedRawPaths = SecondShiftResultMerger.merge(multiPaths, singlePaths)
        
        // 6. FINAL STITCHING: Bridge spatial gaps
        val stitchedPaths = CrossGroupStitcher.stitch(combinedRawPaths)

        return Pair(stitchedPaths, allEdges)
    }
}
