package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.swarm_mgmt

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Job: Swarm State Registry.
 * Responsibility: Maintaining the live count of active agents and the collection of active tracing contexts.
 */
object SwarmStateRegistry {

    private val activeContexts = ConcurrentHashMap<Int, TracingSwarmManager.TracingContext>()
    private val activeAgentCount = AtomicInteger(0)

    fun registerAgent() = activeAgentCount.incrementAndGet()
    fun unregisterAgent() = activeAgentCount.decrementAndGet()
    fun isSwarmIdle(): Boolean = activeAgentCount.get() <= 0
    fun clearAll() {
        activeContexts.clear()
        activeAgentCount.set(0)
    }

    fun addContext(index: Int, context: TracingSwarmManager.TracingContext) {
        activeContexts[index] = context
    }

    fun removeContext(index: Int) {
        activeContexts.remove(index)
    }

    fun getContexts(): Collection<TracingSwarmManager.TracingContext> = activeContexts.values
    fun getContext(index: Int): TracingSwarmManager.TracingContext? = activeContexts[index]
}