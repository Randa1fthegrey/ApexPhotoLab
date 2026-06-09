package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.dispatchers

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.CVPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job4
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.CVPS_Audit
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
        SecondShiftResultManager.wipeAll()
        TracingSwarmManager.wipeAll()
        val vram = SecondShiftVramManager.prepareMasterVram()

        // 2. Perimeter Scanning
        val (bucketCandidates, allEdges) = SecondShiftEdgeScanner.scan(
            width, height, pixels, claimedIndices, vram
        )

        // 3. Work Distribution & Execution
        SecondShiftSwarmDistributor.distributeAndRun(
            bucketCandidates, vram, width, height, pixels
        )

        // 4. Harvesting & Sanitization
        val finalPaths = mutableListOf<List<Point>>()
        for (i in 0 until 11) {
            val territoryBlobs = SecondShiftResultManager.harvest(i)
            if (territoryBlobs.isNotEmpty()) {
                // ColorBucketDiagnostic call removed to clean up syntax
                finalPaths.addAll(routeToPathSanitizer(i, territoryBlobs))
            }
        }

        // 5. Cleanup
        SecondShiftResultManager.wipeAll()
        TracingSwarmManager.wipeAll()

        Pair(finalPaths, allEdges)
    }

    private suspend fun routeToPathSanitizer(index: Int, paths: List<List<Point>>): List<List<Point>> {
        val worker = CVPS_HiringDepartment.getWorkerByColorId(index)
        CVPS_Audit.logCompute(4, index)

        val data = CVPS_job4.SanitizerData(paths)
        worker.runColorTask(4, data)
        return data.result
    }
}