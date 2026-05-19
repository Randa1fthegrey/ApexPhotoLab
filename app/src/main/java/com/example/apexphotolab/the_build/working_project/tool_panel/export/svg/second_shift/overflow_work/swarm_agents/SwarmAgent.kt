package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.overflow_work.swarm_agents

/**
 * Job: Swarm Agent Blueprint.
 * Responsibility: Defining the contract for generic Second Shift workers.
 */
interface SwarmAgent {
    val id: Int
    suspend fun runSwarm()
}
