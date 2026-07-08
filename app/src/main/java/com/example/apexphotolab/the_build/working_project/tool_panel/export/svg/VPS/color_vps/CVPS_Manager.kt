package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job1
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job2_TEST
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job3_Solidify

/**
 * Job: Color VPS System Manager (The Switchboard).
 * Responsibility: Directing compute requests from Color Workers to the correct Job Manual.
 */
object CVPS_Manager {

    suspend fun execute(colorId: Int, jobId: Int, data: Any?) {
        when (jobId) {
            // ==========================================
            // LOGIC FOR JOB 1: RAMPS (Quantize)
            // ==========================================
            1 -> {
                CVPS_job1.execute(colorId, data)
            }
            // ------------------------------------------

            // ==========================================
            // LOGIC FOR JOB 2: DISCOVERY (Trace)
            // ==========================================
            2 -> {
                CVPS_job2_TEST.execute(colorId, data)
            }
            // ------------------------------------------

            // ==========================================
            // LOGIC FOR JOB 3: SOLIDIFICATION (Assemble)
            // ==========================================
            3 -> {
                CVPS_job3_Solidify.execute(colorId, data)
            }
            // ------------------------------------------
        }
    }
}
