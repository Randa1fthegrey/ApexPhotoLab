package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Bitmap
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.CVPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job3
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.CVPS_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.GradientIntelligenceAgency
import kotlinx.coroutines.runBlocking

/**
 * Job: Gradient Scout Orchestrator.
 * Responsibility: Coordinating CVPS-based color scouts to pre-scan for gradient transitions
 * before any structural tracing begins.
 */
object GradientScoutOrchestrator {

    fun run(quantizedImage: Bitmap, originalImage: Bitmap) {
        GradientIntelligenceAgency.clear()
        
        runBlocking {
            for (i in 0 until 10) {
                val worker = CVPS_HiringDepartment.getWorkerByColorId(i)
                CVPS_Audit.logCompute(3, i)
                val data = CVPS_job3.ScoutData(quantizedImage, originalImage)
                worker.runColorTask(3, data)
                data.result.forEach { GradientIntelligenceAgency.record(it) }
            }
        }
    }
}
