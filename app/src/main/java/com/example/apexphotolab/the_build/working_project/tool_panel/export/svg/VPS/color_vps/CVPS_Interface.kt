package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps

/**
 * Job: Color VPS Worker Contract.
 * Responsibility: Defining the shared interface for all Color-based Virtual Processing System workers.
 */
interface CVPS_Interface {
    val colorId: Int
    suspend fun runColorTask(jobId: Int, data: Any?)
}
