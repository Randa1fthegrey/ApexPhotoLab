package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.dispatchers

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.VPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.swarm.SwarmStateRegistry
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.swarm.TracingSwarmManager
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.VRAM_Garage
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
                val bucketVram = VRAM_Garage.getSlotForManager(index)
                val sortedCandidates = points.toList().sortedBy { it.y * 10000 + it.x }
                TracingSwarmManager.registerBucket(
                    index,
                    bucketVram,
                    width,
                    height,
                    pixels,
                    sortedCandidates
                )
            }
        }

        // 2. Hire and Launch the Swarm via VPS for all buckets that have work
        val jobs = (0 until 10).mapNotNull { bucketIndex ->
            val context = SwarmStateRegistry.getContext(bucketIndex) ?: return@mapNotNull null
            val points = mutableListOf<Point>()
            while (context.homeCandidates.isNotEmpty()) {
                context.homeCandidates.poll()?.let { points.add(it) }
            }
            if (points.isEmpty()) return@mapNotNull null

            val assignment = TracingSwarmManager.WorkAssignment(
                bucketIndex, context.vram, context.width, context.height, context.pixels,
                points, context.remainingPixels
            )

            async {
                val worker = VPS_HiringDepartment.getWorkerById(bucketIndex + 1)
                worker.runTask(2, assignment)
            }
        }
        jobs.awaitAll()
    }
}