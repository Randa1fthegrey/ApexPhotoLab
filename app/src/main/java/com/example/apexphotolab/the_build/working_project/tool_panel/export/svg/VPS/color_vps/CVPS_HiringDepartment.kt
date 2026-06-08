package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.Workers.*

/**
 * Job: Color VPS Hiring Department.
 * Responsibility: Managing the persistent squad of 10 Color VPS Workers.
 */
object CVPS_HiringDepartment {

    private val allWorkers: List<CVPS_Interface> = listOf(
        CVPS_Worker0, CVPS_Worker1, CVPS_Worker2, CVPS_Worker3, CVPS_Worker4,
        CVPS_Worker5, CVPS_Worker6, CVPS_Worker7, CVPS_Worker8, CVPS_Worker9
    )

    fun getWorkerByColorId(id: Int): CVPS_Interface {
        return allWorkers[id.coerceIn(0, 9)]
    }
}
