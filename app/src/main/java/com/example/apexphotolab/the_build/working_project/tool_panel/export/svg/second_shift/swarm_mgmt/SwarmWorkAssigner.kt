package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.swarm_mgmt

import android.graphics.Point

/**
 * Job: Swarm Work Assigner.
 * Responsibility: Executing the logic to find and pull slices of work for requesting agents.
 */
object SwarmWorkAssigner {

    private const val SLICE_SIZE = 50

    fun pullSlice(context: TracingSwarmManager.TracingContext): TracingSwarmManager.WorkAssignment {
        val slice = mutableListOf<Point>()
        repeat(SLICE_SIZE) {
            context.homeCandidates.poll()?.let { slice.add(it) }
        }

        return TracingSwarmManager.WorkAssignment(
            context.colorIndex,
            context.vram,
            context.width,
            context.height,
            context.pixels,
            slice,
            context.remainingPixels
        )
    }

    fun findWork(agentId: Int): TracingSwarmManager.WorkAssignment? {
        val homeIndex = agentId - 1
        if (homeIndex in 0..9) {
            val homeContext = SwarmStateRegistry.getContext(homeIndex)
            if (homeContext != null && homeContext.homeCandidates.isNotEmpty()) {
                return pullSlice(homeContext)
            }
        }

        val target = SwarmStateRegistry.getContexts()
            .maxByOrNull { it.homeCandidates.size } ?: return null

        if (target.homeCandidates.isEmpty()) return null

        return pullSlice(target)
    }
}