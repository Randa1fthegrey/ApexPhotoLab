package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.workers
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.VPS_Interface
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.VPS_Manager
object VPS_Worker3 : VPS_Interface {
    override val id = 3
    override suspend fun runTask(jobId: Int, data: Any?) { VPS_Manager.execute(id, jobId, data) }
}
