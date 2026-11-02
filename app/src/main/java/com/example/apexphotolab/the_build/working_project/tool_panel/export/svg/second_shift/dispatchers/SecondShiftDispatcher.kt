package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.dispatchers

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.palette.ColorPalette
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census.CensusReport
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job2_Filter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.swarm.SecondShiftResultManager
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure.SecondShiftVramManager
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.swarm.TracingSwarmManager
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.VRAM_Garage
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
    ): Triple<List<List<Point>>, List<CensusReport>, HashSet<Point>> = withContext(Dispatchers.Default) {

        // 1. Preparation
        SecondShiftResultManager.wipeAll()
        TracingSwarmManager.wipeAll()
        for (i in 0 until 10) {
            VRAM_Garage.wipeSlot(i)
        }
        val vram = SecondShiftVramManager.prepareMasterVram()

        // 2. Perimeter Scanning
        val (bucketCandidates, allEdges) = SecondShiftEdgeScanner.scan(
            width, height, pixels, claimedIndices, vram
        )

        // 3. VRAM DISTILLATION (Thinning each color bucket's VRAM skin)
        for (bucketIndex in 0 until 10) {
            val bucketVram = VRAM_Garage.getSlotForManager(bucketIndex)
            val candidates = bucketCandidates[bucketIndex]
            if (candidates.isNotEmpty()) {
                val edgeSet = HashSet(candidates)
                CVPS_job2_Filter.execute(bucketVram, width, height, edgeSet)
                candidates.retainAll(edgeSet)
            }
        }

        // 4. Work Distribution & Execution
        SecondShiftSwarmDistributor.distributeAndRun(
            bucketCandidates, vram, width, height, pixels
        )

        // 5. Harvesting: Collect traced paths from all color buckets (0..9) and tag with origin color
        val finalPaths = mutableListOf<List<Point>>()
        val finalReports = mutableListOf<CensusReport>()
        for (bucketIndex in 0 until 10) {
            val territoryBlobs = SecondShiftResultManager.harvest(bucketIndex)
            if (territoryBlobs.isNotEmpty()) {
                val bucketColor = ColorPalette.PALETTE[bucketIndex]
                for (blob in territoryBlobs) {
                    finalPaths.add(blob)
                    finalReports.add(
                        CensusReport(
                            dominantColor = bucketColor,
                            secondaryColor = bucketColor,
                            totalPixels = blob.size,
                            blendRatio = 0f,
                            complexityScore = 0
                        )
                    )
                }
            }
        }

        // 6. Cleanup
        SecondShiftResultManager.wipeAll()
        TracingSwarmManager.wipeAll()

        Triple(finalPaths, finalReports, allEdges)
    }
}
