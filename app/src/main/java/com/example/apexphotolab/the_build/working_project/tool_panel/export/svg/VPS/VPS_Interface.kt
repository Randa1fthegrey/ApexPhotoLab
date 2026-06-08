package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS

/**
 * Job: VPS Worker Contract.
 * Responsibility: Defining the shared interface for all Virtual Processing System workers.
 */
interface VPS_Interface {
    val id: Int
    suspend fun runTask(jobId: Int, data: Any?)
}
