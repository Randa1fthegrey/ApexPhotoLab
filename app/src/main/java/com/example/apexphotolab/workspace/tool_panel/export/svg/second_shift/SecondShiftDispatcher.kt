package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift

import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export._temp_tools.SanitizerObserver
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
 * Updated: Now includes the "Power Wash" protocol to stop memory pollution.
 */
object SecondShiftDispatcher {

    /**
     * Traces all color groups. 
     * Uses VRAM bitmasks to handle millions of pixels with near-zero heap overhead.
     */
    suspend fun traceInParallel(
        pixelGroups: List<List<Int>>,
        width: Int,
        height: Int
    ): Pair<List<List<Point>>, HashSet<Point>> = withContext(Dispatchers.Default) {

        val highways = CoreHighwayFactory.coreHighways

        val jobs = pixelGroups.mapIndexed { index, indices ->
            if (indices.isEmpty()) return@mapIndexed null

            val dispatcher = if (highways.isNotEmpty()) highways[index % highways.size] else Dispatchers.Default

            async(dispatcher) {
                // POWER WASH: Erase any ghost data from previous runs
                VRAM_Garage.wipeSlot(index)

                val vram = VRAM_Garage.getSlotForManager(index)

                // 1. Convert to VRAM Bitmask (Zero Heap) - RAW DATA
                VRAM_BlobConverter.convertToVRAM(indices, vram)

                // 2. Edge Finding from RAW VRAM Bitmask (No detail loss yet!)
                val edges = VRAM_EdgeFinder.findEdgesVRAM(vram, width, height)

                // 3. Tracing (Routing to specialist) - Captured high-fidelity paths
                val rawPaths = TracingRouter.route(index, edges, vram, width)

                // 4. PATH-BASED SANITIZATION: Clean up loose ends AFTER shapes are made
                SanitizerObserver.logPathInput(index, rawPaths)
                val cleanPaths = routeToPathSanitizer(index, rawPaths)
                SanitizerObserver.logPathOutput(index, cleanPaths)

                Pair(cleanPaths, edges)
            }
        }.filterNotNull()

        val results = jobs.awaitAll()
        return@withContext ResultAggregator.aggregate(results)
    }

    /**
     * Routes the completed paths to specialized cleaning teams.
     */
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
