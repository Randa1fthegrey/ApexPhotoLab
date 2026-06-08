package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.overflow_work

import android.graphics.Point
import java.nio.ByteBuffer
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * Job: Tracing Swarm Manager.
 * Responsibility: Coordinating work-stealing for the Second Shift tracing pipeline.
 * Ensures that when a color group finishes its primary work, its CPU core is
 * immediately repurposed to help lagging color groups.
 */
object TracingSwarmManager {

    private val activeContexts = ConcurrentHashMap<Int, TracingContext>()
    private val activeAgentCount = AtomicInteger(0)

    class TracingContext(
        val colorIndex: Int,
        val vram: ByteBuffer,
        val width: Int,
        val height: Int,
        val pixels: IntArray,
        val homeCandidates: ConcurrentLinkedQueue<Point>,
        val remainingPixels: MutableSet<Point>
    )

    fun registerAgent() {
        activeAgentCount.incrementAndGet()
    }

    fun unregisterAgent() {
        activeAgentCount.decrementAndGet()
    }

    fun isSwarmIdle(): Boolean {
        return activeAgentCount.get() <= 0
    }

    fun wipeAll() {
        activeContexts.clear()
        activeAgentCount.set(0)
    }

    /**
     * Registers a color bucket's data so it can be assisted by other workers.
     */
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

        // Using a thread-safe set to prevent multiple workers from tracing the same shape
        val remaining = Collections.newSetFromMap(ConcurrentHashMap<Point, Boolean>())
        remaining.addAll(candidates)

        activeContexts[index] = TracingContext(index, vram, width, height, pixels, queue, remaining)
    }

    /**
     * Called by a finished tracer to find a new assignment.
     */
    fun checkIn(agentId: Int): WorkAssignment? {
        // Priority 1: Check if its own "home" still has work (Agents 1-10 only)
        val homeIndex = agentId - 1
        if (homeIndex in 0..9) {
            val homeContext = activeContexts[homeIndex]
            if (homeContext != null && homeContext.homeCandidates.isNotEmpty()) {
                return pullSlice(homeContext)
            }
        }

        // Priority 2: Look for any color group with the most remaining work
        val target = activeContexts.values
            .maxByOrNull { it.homeCandidates.size } ?: return null

        if (target.homeCandidates.isEmpty()) return null

        return pullSlice(target)
    }

    private fun pullSlice(context: TracingContext): WorkAssignment {
        val slice = mutableListOf<Point>()
        repeat(SLICE_SIZE) {
            context.homeCandidates.poll()?.let { slice.add(it) }
        }

        return WorkAssignment(
            context.colorIndex,
            context.vram,
            context.width,
            context.height,
            context.pixels,
            slice,
            context.remainingPixels
        )
    }

    /**
     * Used by Primary workers to take a significant initial chunk of their own color.
     */
    fun getPrimaryWork(index: Int): List<Point> {
        val context = activeContexts[index] ?: return emptyList()
        val primaryWork = mutableListOf<Point>()

        // Take a large portion initially (e.g., 60%) to reduce overhead, leaving 40% for stealing
        val amount = (context.homeCandidates.size * 0.6).toInt().coerceAtLeast(1)
        repeat(amount) {
            context.homeCandidates.poll()?.let { primaryWork.add(it) }
        }
        return primaryWork
    }

    fun unregisterBucket(index: Int) {
        activeContexts.remove(index)
    }

    fun getSharedRemainingSet(index: Int): MutableSet<Point>? {
        return activeContexts[index]?.remainingPixels
    }

    data class WorkAssignment(
        val colorIndex: Int,
        val vram: ByteBuffer,
        val width: Int,
        val height: Int,
        val pixels: IntArray,
        val candidates: List<Point>,
        val sharedRemainingSet: MutableSet<Point>
    )

    private const val SLICE_SIZE = 50
    private const val STEAL_THRESHOLD = 20
}