package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.swarm_mgmt

import android.graphics.Point
import java.nio.ByteBuffer
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Job: Tracing Swarm Manager (The Orchestrator).
 * Responsibility: Coordinating work-stealing for the Second Shift by delegating state registry and work assignment.
 */
object TracingSwarmManager {

    class TracingContext(
        val colorIndex: Int,
        val vram: ByteBuffer,
        val width: Int,
        val height: Int,
        val pixels: IntArray,
        val homeCandidates: ConcurrentLinkedQueue<Point>,
        val remainingPixels: MutableSet<Point>
    )

    data class WorkAssignment(
        val colorIndex: Int,
        val vram: ByteBuffer,
        val width: Int,
        val height: Int,
        val pixels: IntArray,
        val candidates: List<Point>,
        val sharedRemainingSet: MutableSet<Point>
    )

    fun registerAgent() = SwarmStateRegistry.registerAgent()
    fun unregisterAgent() = SwarmStateRegistry.unregisterAgent()
    fun isSwarmIdle(): Boolean = SwarmStateRegistry.isSwarmIdle()
    fun wipeAll() = SwarmStateRegistry.clearAll()

    fun registerBucket(
        index: Int,
        vram: ByteBuffer,
        width: Int,
        height: Int,
        pixels: IntArray,
        candidates: List<Point>
    ) {
        val queue = ConcurrentLinkedQueue<Point>()
        queue.addAll(candidates)

        val remaining = Collections.newSetFromMap(ConcurrentHashMap<Point, Boolean>())
        remaining.addAll(candidates)

        val context = TracingContext(index, vram, width, height, pixels, queue, remaining)
        SwarmStateRegistry.addContext(index, context)
    }

    fun checkIn(agentId: Int): WorkAssignment? = SwarmWorkAssigner.findWork(agentId)

    fun getPrimaryWork(index: Int): List<Point> {
        val context = SwarmStateRegistry.getContext(index) ?: return emptyList()
        val primaryWork = mutableListOf<Point>()

        val amount = (context.homeCandidates.size * 0.6).toInt().coerceAtLeast(1)
        repeat(amount) {
            context.homeCandidates.poll()?.let { primaryWork.add(it) }
        }
        return primaryWork
    }

    fun unregisterBucket(index: Int) = SwarmStateRegistry.removeContext(index)

    fun getSharedRemainingSet(index: Int): MutableSet<Point>? = SwarmStateRegistry.getContext(index)?.remainingPixels
}