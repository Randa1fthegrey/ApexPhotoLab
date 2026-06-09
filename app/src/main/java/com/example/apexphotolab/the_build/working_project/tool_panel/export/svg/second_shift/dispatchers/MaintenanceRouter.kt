package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.dispatchers

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.CVPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job7
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.CVPS_Audit
import java.nio.ByteBuffer

/**
 * Job: Maintenance Router.
 * Responsibility: Routing path fragments to the CVPS Job 7 for solidification.
 */
object MaintenanceRouter {

    suspend fun route(
        index: Int,
        fragments: List<List<Point>>,
        vram: ByteBuffer,
        width: Int,
        height: Int,
        pixels: IntArray
    ): List<List<Point>> {

        // CVPS CALL INSTEAD OF LEGACY
        val worker = CVPS_HiringDepartment.getWorkerByColorId(index)
        CVPS_Audit.logCompute(7, index)

        val data = CVPS_job7.SolidificationData(fragments, vram, width, height, pixels)
        worker.runColorTask(7, data)

        return data.result
    }
}