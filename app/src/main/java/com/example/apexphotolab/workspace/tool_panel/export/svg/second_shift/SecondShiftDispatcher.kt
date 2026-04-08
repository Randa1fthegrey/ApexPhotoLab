package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift

import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg._temp_tools.SanitizerObserver
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.sanitizers.*
import com.example.apexphotolab.workspace.tool_panel.export.svg.utils.CoreHighwayFactory
import com.example.apexphotolab.workspace.tool_panel.export.svg.utils.VRAM_Garage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Job #1: The Team Manager.
 * Final VRAM-Optimized Version: Orchestrates 100% off-heap blob processing.
 * Updated: Now accepts raw pixels for gradient intelligence reporting.
 */
object SecondShiftDispatcher {

    /**
     * Traces all color groups.
     * Uses VRAM bitmasks to handle millions of pixels with near-zero heap overhead.
     */
    suspend fun traceInParallel(
        pixelGroups: List<List<Int>>,
        width: Int,
        height: Int,
        pixels: IntArray // Added for gradient reporting
    ): Pair<List<List<Point>>, HashSet<Point>> = withContext(Dispatchers.Default) {

        val highways = CoreHighwayFactory.coreHighways

        val jobs = pixelGroups.mapIndexed { index, indices ->
            if (indices.isEmpty()) return@mapIndexed null

            val dispatcher = if (highways.isNotEmpty()) highways[index % highways.size] else Dispatchers.Default

            async(dispatcher) {
                VRAM_Garage.wipeSlot(index)
                val vram = VRAM_Garage.getSlotForManager(index)

                VRAM_BlobConverter.convertToVRAM(indices, vram)
                
                // --- STEP: PAVE GOLDEN RAILS ---
                // Before finding edges, we burn the "True North" spines into the VRAM.
                val reports = GradientIntelligenceAgency.getReportsForScout(index)
                reports.forEach { report ->
                    VRAM_Paver.pave(report.path, vram, width, height)
                }

                val edges = VRAM_EdgeFinder.findEdgesVRAM(vram, width, height)

                // Pass pixels to the router and specialists
                val rawPaths = TracingRouter.route(index, edges, vram, width, height, pixels)

                SanitizerObserver.logPathInput(index, rawPaths)
                val cleanPaths = routeToPathSanitizer(index, rawPaths)
                SanitizerObserver.logPathOutput(index, cleanPaths)

                Pair(cleanPaths, edges)
            }
        }.filterNotNull()

        val results = jobs.awaitAll()
        return@withContext ResultAggregator.aggregate(results)
    }

    private fun routeToPathSanitizer(index: Int, paths: List<List<Point>>): List<List<Point>> {
        return when (index) {
            0 -> RedBlobSanitizer.sanitizePaths(paths)
            1 -> GreenBlobSanitizer.sanitizePaths(paths)
            2 -> BlueBlobSanitizer.sanitizePaths(paths)
            3 -> YellowBlobSanitizer.sanitizePaths(paths)
            4 -> CyanBlobSanitizer.sanitizePaths(paths)
            5 -> MagentaBlobSanitizer.sanitizePaths(paths)
            6 -> WhiteBlobSanitizer.sanitizePaths(paths)
            7 -> AlphaBlobSanitizer.sanitizePaths(paths)
            8 -> BlackBlobSanitizer.sanitizePaths(paths)
            9 -> GreyBlobSanitizer.sanitizePaths(paths)
            else -> paths
        }
    }
}
