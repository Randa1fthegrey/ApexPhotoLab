package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * Job: Swarm Broker.
 * Responsibility: Coordinating work-stealing across all 30 workers to ensure zero idle time.
 */
object SwarmBroker {

    // A list of 10 queues, one for each color group
    private val colorGroupQueues = List(10) { ConcurrentLinkedQueue<SwarmTask>() }
    
    // Tracks how many tasks are remaining in total
    private val totalTasksRemaining = AtomicInteger(0)

    /**
     * Initializes the broker with work tasks for the 10 color groups.
     */
    fun setup(tasksByGroup: List<List<SwarmTask>>) {
        reset()
        tasksByGroup.forEachIndexed { groupIndex, tasks ->
            tasks.forEach { task ->
                colorGroupQueues[groupIndex].add(task)
                totalTasksRemaining.incrementAndGet()
            }
        }
    }

    /**
     * Resets the broker for a new export run.
     */
    fun reset() {
        colorGroupQueues.forEach { it.clear() }
        totalTasksRemaining.set(0)
    }

    /**
     * Request a piece of work. 
     * Workers first try to take from their "Home" group, then steal from the largest pile.
     */
    fun requestWork(preferredGroupIndex: Int): SwarmTask? {
        if (totalTasksRemaining.get() <= 0) return null

        // 1. Try to take from the preferred (Home) group first
        if (preferredGroupIndex in 0..9) {
            val homeTask = colorGroupQueues[preferredGroupIndex].poll()
            if (homeTask != null) {
                totalTasksRemaining.decrementAndGet()
                return homeTask
            }
        }

        // 2. WORK STEALING: If home is empty, find the group with the most work left
        var largestQueueIndex = -1
        var largestSize = 0

        for (i in 0 until 10) {
            val size = colorGroupQueues[i].size
            if (size > largestSize) {
                largestSize = size
                largestQueueIndex = i
            }
        }

        if (largestQueueIndex != -1) {
            val stolenTask = colorGroupQueues[largestQueueIndex].poll()
            if (stolenTask != null) {
                totalTasksRemaining.decrementAndGet()
                return stolenTask
            }
        }

        return null
    }

    /**
     * Represents a single unit of work (a slice of pixel indices for a specific color).
     */
    data class SwarmTask(
        val groupIndex: Int,
        val pixelIndices: List<Int>
    )
}
