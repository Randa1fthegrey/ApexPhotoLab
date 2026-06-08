package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.vps_jobs.VPS_job1
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.vps_jobs.VPS_job2
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.vps_jobs.VPS_job3
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.vps_jobs.VPS_job4

/**
 * Job: VPS System Manager (The Switchboard).
 * Responsibility: Directing compute requests from Workers to the correct Job Manual.
 * 
 * This file acts as the "Job Listing" for the entire Virtual Processing System.
 */
object VPS_Manager {

    suspend fun execute(workerId: Int, jobId: Int, data: Any?) {
        when (jobId) {
            // ==========================================
            // LOGIC FOR JOB 1: QUANTIZATION
            // ==========================================
            1 -> {
                VPS_job1.execute(workerId, data)
            }
            // ------------------------------------------

            // ==========================================
            // LOGIC FOR JOB 2: PATH TRACING
            // ==========================================
            2 -> {
                VPS_job2.execute(workerId, data)
            }
            // ------------------------------------------

            // ==========================================
            // LOGIC FOR JOB 3: CENSUS
            // ==========================================
            3 -> {
                VPS_job3.execute(workerId, data)
            }
            // ------------------------------------------

            // ==========================================
            // LOGIC FOR JOB 4: BLENDING
            // ==========================================
            4 -> {
                VPS_job4.execute(workerId, data)
            }
            // ------------------------------------------
        }
    }
}
