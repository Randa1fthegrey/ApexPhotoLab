package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift

import android.graphics.Point
import com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.sanitizers.AlphaBlobSanitizer
import com.example.apexphotolab.workspace.toolbars.export.svg.utils.CoreHighwayFactory
import com.example.apexphotolab.workspace.toolbars.export.svg.utils.VRAM_Garage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Job #1: The Team Manager.
 * Final VRAM-Optimized Version: Orchestrates 100% off-heap blob processing.
 * Heap usage is now independent of image size.
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
                val vram = VRAM_Garage.getSlotForManager(index)

                // 1. Convert to VRAM Bitmask (Zero Heap)
                VRAM_BlobConverter.convertToVRAM(indices, vram)

                // 2. Sanitize in VRAM (Zero Heap)
                if (index == 7) {
                    AlphaBlobSanitizer.sanitizeInPlace(indices, width, height, vram)
                } else {
                    // Universal Noise Filter & Healing logic entirely in VRAM
                    BlobHealer.healInPlaceVRAM(width, height, vram)
                }

                // 3. Edge Finding from VRAM Bitmask (Low Heap - Edges only)
                val edges = VRAM_EdgeFinder.findEdgesVRAM(vram, width, height)

                // 4. Tracing (Routing to specialist)
                val paths = TracingRouter.route(index, edges, vram, width)

                Pair(paths, edges)
            }
        }.filterNotNull()

        val results = jobs.awaitAll()
        return@withContext ResultAggregator.aggregate(results)
    }
}
