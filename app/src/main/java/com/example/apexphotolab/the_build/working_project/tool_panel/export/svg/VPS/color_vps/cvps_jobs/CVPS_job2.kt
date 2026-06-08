package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.AlphaPathTracer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.BlackPathTracer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.BluePathTracer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.CyanPathTracer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.GreenPathTracer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.GreyPathTracer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.MagentaPathTracer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.RedPathTracer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.WhitePathTracer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.YellowPathTracer
import java.nio.ByteBuffer

/**
 * Job: CVPS Job 2 - Discovery.
 * Responsibility: Coordinating path discovery for specific color groups.
 */
object CVPS_job2 {

    data class DiscoveryData(
        val edges: HashSet<Point>,
        val vram: ByteBuffer,
        val width: Int,
        val pixels: IntArray,
        val sharedRemainingSet: MutableSet<Point>? = null,
        val specificCandidates: List<Point>? = null,
        var result: List<List<Point>> = emptyList()
    )

    fun execute(colorId: Int, data: Any?) {
        val dData = data as? DiscoveryData ?: return
        val candidates = dData.specificCandidates ?: dData.edges.sortedBy { it.y * 10000 + it.x }

        dData.result = when (colorId) {
            0 -> RedPathTracer.trace(candidates, dData.vram, dData.width, dData.pixels, dData.sharedRemainingSet)
            1 -> GreenPathTracer.trace(candidates, dData.vram, dData.width, dData.pixels, dData.sharedRemainingSet)
            2 -> BluePathTracer.trace(candidates, dData.vram, dData.width, dData.pixels, dData.sharedRemainingSet)
            3 -> YellowPathTracer.trace(candidates, dData.vram, dData.width, dData.pixels, dData.sharedRemainingSet)
            4 -> CyanPathTracer.trace(candidates, dData.vram, dData.width, dData.pixels, dData.sharedRemainingSet)
            5 -> MagentaPathTracer.trace(candidates, dData.vram, dData.width, dData.pixels, dData.sharedRemainingSet)
            6 -> WhitePathTracer.trace(candidates, dData.vram, dData.width, dData.pixels, dData.sharedRemainingSet)
            7 -> AlphaPathTracer.trace(candidates, dData.vram, dData.width, dData.pixels, dData.sharedRemainingSet)
            8 -> BlackPathTracer.trace(candidates, dData.vram, dData.width, dData.pixels, dData.sharedRemainingSet)
            9 -> GreyPathTracer.trace(candidates, dData.vram, dData.width, dData.pixels, dData.sharedRemainingSet)
            else -> emptyList()
        }
    }
}