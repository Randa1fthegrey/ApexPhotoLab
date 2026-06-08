package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.Workers

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.CVPS_Interface
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.CVPS_Manager

object CVPS_Worker0 : CVPS_Interface {
    override val colorId = 0
    override suspend fun runColorTask(jobId: Int, data: Any?) {
        CVPS_Manager.execute(colorId, jobId, data)
    }
}
