package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.ColorBucketDiagnostic
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.overflow_work.OverflowRouter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.overflow_work.TracingSwarmManager
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.overflow_work.swarm_agents.SwarmHiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.sanitizers.*
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram.VRAM_BlobConverter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram.VRAM_EdgeFinder
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram.VRAM_Paver
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.CoreChecker
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.CoreHighwayFactory
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.VRAM_Garage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * Job: Second Shift Dispatcher.
 * Responsibility: Orchestrating parallel VRAM-based blob processing for the 10 color groups.
 * Ensures each group is processed as a whole to maintain geometric integrity.
 */
object SecondShiftDispatcher {

    suspend fun traceInParallel(
        pixelGroups: List<List<Int>>,
        width: Int,
        height: Int,
        pixels: IntArray,
        claimedIndices: Set<Int>
    ): Pair<List<List<Point>>, HashSet<Point>> = withContext(Dispatchers.Default) {

        OverflowRouter.wipeAll()
        val allEdgesMap = ConcurrentHashMap<Int, HashSet<Point>>()

        // 1. PRE-REGISTRATION: Prepare all 10 buckets for the swarm
        pixelGroups.forEachIndexed { index, indices ->
            val filteredIndices = if (claimedIndices.isEmpty()) indices else indices.filter { !claimedIndices.contains(it) }
            if (filteredIndices.isEmpty()) return@forEachIndexed

            VRAM_Garage.wipeSlot(index)
            val vram = VRAM_Garage.getSlotForManager(index)

            VRAM_BlobConverter.convertToVRAM(filteredIndices, vram)

            val reports = GradientIntelligenceAgency.getReportsForScout(index)
            reports.forEach { report ->
                VRAM_Paver.pave(report.path, vram, width, height)
            }

            val edges = VRAM_EdgeFinder.findEdgesVRAM(vram, width, height)
            allEdgesMap[index] = edges

            val candidates = edges.toList().sortedBy { it.y * 10000 + it.x }
            TracingSwarmManager.registerBucket(index, vram, width, height, pixels, candidates)
        }

        // 2. SWARM DISPATCH: Launch workers based on CPU core count
        val coreCount = CoreChecker.coreCount
        val highways = CoreHighwayFactory.coreHighways
        val agents = SwarmHiringDepartment.hireAgents(coreCount)

        val swarmJobs = agents.mapIndexed { i, agent ->
            val dispatcher = if (highways.isNotEmpty()) highways[i % highways.size] else Dispatchers.Default
            async(dispatcher) {
                agent.runSwarm()
            }
        }

        // 3. THE AIRLOCK WAIT: Wait for the entire generic swarm to finish all work
        swarmJobs.awaitAll()

        // 4. THE HARVEST: Now that the swarm is idle, collect the organized results
        val finalPaths = mutableListOf<List<Point>>()
        val totalEdges = HashSet<Point>()

        for (i in 0 until 10) {
            val accruedBlobs = OverflowRouter.harvest(i)
            ColorBucketDiagnostic.logShift2Harvest(i, accruedBlobs.size)

            val sanitizedBlobs = routeToPathSanitizer(i, accruedBlobs)
            finalPaths.addAll(sanitizedBlobs)
            
            val edgesForBucket = allEdgesMap[i]
            if (edgesForBucket != null) {
                totalEdges.addAll(edgesForBucket)
            }
            TracingSwarmManager.unregisterBucket(i)
        }

        OverflowRouter.wipeAll()

        Pair(finalPaths, totalEdges)
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
