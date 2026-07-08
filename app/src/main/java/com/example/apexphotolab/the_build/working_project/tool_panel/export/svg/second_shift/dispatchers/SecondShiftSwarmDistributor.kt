package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.dispatchers

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.VPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.VPS_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.swarm_mgmt.SwarmStateRegistry
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.swarm_mgmt.TracingSwarmManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.nio.ByteBuffer

/**
 * Job: Second Shift Swarm Distributor.
 * Responsibility: Organizing the color buckets and launching the parallel Swarm Agents
 * to perform the actual path tracing.
 */
object SecondShiftSwarmDistributor {

    suspend fun distributeAndRun(
        bucketCandidates: List<MutableSet<Point>>,
        vram: ByteBuffer,
        width: Int,
        height: Int,
        pixels: IntArray
    ) = coroutineScope {

        // 1. Register all buckets that have work
        bucketCandidates.forEachIndexed { index, points ->
            if (points.isNotEmpty()) {
                val sortedCandidates = points.toList().sortedBy { it.y * 10000 + it.x }
                TracingSwarmManager.registerBucket(
                    index,
                    vram,
                    width,
                    height,
                    pixels,
                    sortedCandidates
                )
            }
        }

        // 2. Hire and Launch the Swarm via VPS
        // DETERMINISTIC ISOLATION: Use only Worker 1 to ensure a stable trace.
        val greyContext = SwarmStateRegistry.getContext(9)
        val allPoints = mutableListOf<Point>()
        greyContext?.let { ctx ->
            while (ctx.homeCandidates.isNotEmpty()) {
                ctx.homeCandidates.poll()?.let { allPoints.add(it) }
            }
        }

        val myAssignment = if (allPoints.isNotEmpty()) {
            TracingSwarmManager.WorkAssignment(
                9, greyContext!!.vram, greyContext.width, greyContext.height, greyContext.pixels,
                allPoints, greyContext.remainingPixels
            )
        } else null

        val worker = VPS_HiringDepartment.getWorkerById(1)
        VPS_Audit.logShiftHandoff(2)
        VPS_Audit.logCompute(2, worker.id)
        worker.runTask(2, myAssignment)
    }
}