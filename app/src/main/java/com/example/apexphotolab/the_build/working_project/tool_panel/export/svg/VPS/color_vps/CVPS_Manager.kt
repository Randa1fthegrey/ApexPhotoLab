package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job1
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job2
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job3
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job4
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job5
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job6

/**
 * Job: Color VPS System Manager (The Switchboard).
 * Responsibility: Directing compute requests from Color Workers to the correct Job Manual.
 */
object CVPS_Manager {

    suspend fun execute(colorId: Int, jobId: Int, data: Any?) {
        when (jobId) {
            // ==========================================
            // LOGIC FOR JOB 1: RAMPS
            // ==========================================
            1 -> {
                CVPS_job1.execute(colorId, data)
            }
            // ------------------------------------------

            // ==========================================
            // LOGIC FOR JOB 2: DISCOVERY
            // ==========================================
            2 -> {
                CVPS_job2.execute(colorId, data)
            }
            // ------------------------------------------

            // ==========================================
            // LOGIC FOR JOB 3: GRADIENT SCOUTS
            // ==========================================
            3 -> {
                CVPS_job3.execute(colorId, data)
            }
            // ------------------------------------------

            // ==========================================
            // LOGIC FOR JOB 4: SANITIZERS
            // ==========================================
            4 -> {
                CVPS_job4.execute(colorId, data)
            }
            // ------------------------------------------

            // ==========================================
            // LOGIC FOR JOB 5: CONSOLIDATORS
            // ==========================================
            5 -> {
                CVPS_job5.execute(colorId, data)
            }
            // ------------------------------------------

            // ==========================================
            // LOGIC FOR JOB 6: BLENDING
            // ==========================================
            6 -> {
                CVPS_job6.execute(colorId, data)
            }
            // ------------------------------------------
        }
    }
}
