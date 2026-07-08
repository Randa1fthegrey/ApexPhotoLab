package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.dispatchers

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.CVPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job2_TEST
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.CVPS_Audit
import java.nio.ByteBuffer

/**
 * Job: Tracing Router.
 * Responsibility: Routing edges through the Discovery and Maintenance pipeline for a given color group using the CVPS.
 */
object TracingRouter {

    suspend fun route(
        index: Int,
        edges: HashSet<Point>,
        vram: ByteBuffer,
        width: Int,
        height: Int,
        pixels: IntArray,
        sharedRemainingSet: MutableSet<Point>? = null,
        specificCandidates: List<Point>? = null
    ): List<List<Point>> {

        val worker = CVPS_HiringDepartment.getWorkerByColorId(index)
        CVPS_Audit.logCompute(2, index)

        val data = CVPS_job2_TEST.DiscoveryData(
            edges = edges,
            vram = vram,
            width = width,
            height = height,
            pixels = pixels,
            sharedRemainingSet = sharedRemainingSet,
            specificCandidates = specificCandidates
        )

        worker.runColorTask(2, data)
        return data.result
    }
}
