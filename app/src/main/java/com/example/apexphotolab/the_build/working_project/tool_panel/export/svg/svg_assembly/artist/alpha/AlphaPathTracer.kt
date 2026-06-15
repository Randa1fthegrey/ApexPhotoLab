package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.alpha

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.CVPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job2
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.AlphaGradientDetector
import kotlinx.coroutines.runBlocking
import java.nio.ByteBuffer

/**
 * Job: Alpha Path Tracer.
 * Responsibility: Coordinating with the CVPS to trace discovered alpha edge coordinates into ordered paths.
 */
object AlphaPathTracer {

    fun trace(edges: HashSet<Point>, vram: ByteBuffer, info: AlphaGradientDetector.AlphaGradientInfo): List<List<Point>> {
        val worker = CVPS_HiringDepartment.getWorkerByColorId(7) // Alpha
        val data = CVPS_job2.DiscoveryData(
            edges = edges,
            vram = vram,
            width = info.width,
            height = info.height,
            pixels = info.pixels
        )

        runBlocking {
            worker.runColorTask(2, data)
        }
        return data.result
    }
}