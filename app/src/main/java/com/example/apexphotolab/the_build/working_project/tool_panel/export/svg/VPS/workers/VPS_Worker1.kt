package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.workers

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.VPS_Interface
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.VPS_Manager

/**
 * Job: VPS Worker #1.
 * Responsibility: Executing assigned tasks by delegating to the VPS Manager.
 */
object VPS_Worker1 : VPS_Interface {
    override val id = 1
    override suspend fun runTask(jobId: Int, data: Any?) {
        VPS_Manager.execute(id, jobId, data)
    }
}
