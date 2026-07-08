package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.dispatchers

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job2_Filter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.CVPS_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.SVG_Unified_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure.SecondShiftResultManager
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram.SecondShiftVramManager
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.swarm_mgmt.TracingSwarmManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Job: Second Shift Orchestrator & Harvester.
 * Responsibility: Coordinating specialized workers (VRAM, Noise, Scanner, Swarm) using the CVPS for sanitization.
 */
object SecondShiftDispatcher {

    suspend fun traceInParallel(
        pixelGroups: List<List<Int>>,
        width: Int,
        height: Int,
        pixels: IntArray,
        claimedIndices: Set<Int>
    ): Pair<List<List<Point>>, HashSet<Point>> = withContext(Dispatchers.Default) {

        // 1. Preparation
        CVPS_Audit.clearTallies()
        SecondShiftResultManager.wipeAll()
        TracingSwarmManager.wipeAll()
        val vram = SecondShiftVramManager.prepareMasterVram()

        // 2. Perimeter Scanning
        SVG_Unified_Audit.logHandoff("SecondShiftDispatcher", "SecondShiftEdgeScanner")
        val (bucketCandidates, allEdges) = SecondShiftEdgeScanner.scan(
            width, height, pixels, claimedIndices, vram
        )

        // 3. VRAM DISTILLATION (Thinning the tightrope)
        // We run the filter BEFORE tracing to ensure workers only see a 1-pixel skin.
        SVG_Unified_Audit.logHandoff("SecondShiftDispatcher", "CVPS_job2_Filter", "Thinning Buckets")
        CVPS_job2_Filter.execute(vram, width, height, allEdges, bucketCandidates)

        // 4. Work Distribution & Execution
        SVG_Unified_Audit.logHandoff("SecondShiftDispatcher", "SecondShiftSwarmDistributor", "Tracing Distilled Edges")
        SecondShiftSwarmDistributor.distributeAndRun(
            bucketCandidates, vram, width, height, pixels
        )

        // 5. Harvesting (ISOLATION MODE: Only Grey ID 9)
        val finalPaths = mutableListOf<List<Point>>()
        val territoryBlobs = SecondShiftResultManager.harvest(9)
        if (territoryBlobs.isNotEmpty()) {
            finalPaths.addAll(territoryBlobs)
        }

        // 6. Cleanup
        SecondShiftResultManager.wipeAll()
        TracingSwarmManager.wipeAll()

        CVPS_Audit.reportFinalTallies()

        Pair(finalPaths, allEdges)
    }
}
