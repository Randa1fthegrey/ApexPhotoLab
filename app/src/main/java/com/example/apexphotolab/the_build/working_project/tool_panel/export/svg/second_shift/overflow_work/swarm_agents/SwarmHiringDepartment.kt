package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.overflow_work.swarm_agents

/**
 * Job: Swarm Hiring Department.
 * Responsibility: Providing a list of generic workers based on the requested count.
 */
object SwarmHiringDepartment {

    fun hireAgents(count: Int): List<SwarmAgent> {
        val safeCount = count.coerceIn(0, 30)
        return allAgents.take(safeCount)
    }

    private val allAgents = listOf(
        SwarmAgent1, SwarmAgent2, SwarmAgent3, SwarmAgent4, SwarmAgent5,
        SwarmAgent6, SwarmAgent7, SwarmAgent8, SwarmAgent9, SwarmAgent10,
        SwarmAgent11, SwarmAgent12, SwarmAgent13, SwarmAgent14, SwarmAgent15,
        SwarmAgent16, SwarmAgent17, SwarmAgent18, SwarmAgent19, SwarmAgent20,
        SwarmAgent21, SwarmAgent22, SwarmAgent23, SwarmAgent24, SwarmAgent25,
        SwarmAgent26, SwarmAgent27, SwarmAgent28, SwarmAgent29, SwarmAgent30
    )
}
