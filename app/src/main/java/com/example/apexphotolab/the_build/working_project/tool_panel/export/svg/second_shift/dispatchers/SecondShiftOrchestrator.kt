package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.dispatchers

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure.SecondShiftPixelExtractor

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census.CensusReport

/**
 * Job: Second Shift Orchestrator (The Master Manager).
 * Responsibility: Coordinating the specialized scouts, tracers, and stitchers to produce watertight geometry.
 */
object SecondShiftOrchestrator {

    suspend fun run(
        quantizedImage: Bitmap, 
        originalImage: Bitmap,
        colorGroups: List<List<Int>>
    ): Triple<List<List<Point>>, List<CensusReport>, HashSet<Point>> {
        val width = quantizedImage.width
        val height = quantizedImage.height
        
        // 1. DATA PREPARATION
        val pixels = SecondShiftPixelExtractor.extract(quantizedImage)

        val claimedIndices = emptySet<Int>()

        // 2. STANDARD TEAM: Process sorted color groups
        return SecondShiftDispatcher.traceInParallel(colorGroups, width, height, pixels, claimedIndices)
    }
}
