package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.tracer_team2.*
import java.nio.ByteBuffer

/**
 * Job: Maintenance Router.
 * Responsibility: Routing path fragments to the correct Team 2 tracer for solidification.
 */
object MaintenanceRouter {

    fun route(
        index: Int,
        fragments: List<List<Point>>,
        vram: ByteBuffer,
        width: Int,
        height: Int,
        pixels: IntArray
    ): List<List<Point>> {
        return when (index) {
            0 -> RedPathTracerTeam2.solidify(fragments, vram, width, height, pixels)
            1 -> GreenPathTracerTeam2.solidify(fragments, vram, width, height, pixels)
            2 -> BluePathTracerTeam2.solidify(fragments, vram, width, height, pixels)
            3 -> YellowPathTracerTeam2.solidify(fragments, vram, width, height, pixels)
            4 -> CyanPathTracerTeam2.solidify(fragments, vram, width, height, pixels)
            5 -> MagentaPathTracerTeam2.solidify(fragments, vram, width, height, pixels)
            6 -> WhitePathTracerTeam2.solidify(fragments, vram, width, height, pixels)
            7 -> AlphaPathTracerTeam2.solidify(fragments, vram, width, height, pixels)
            8 -> BlackPathTracerTeam2.solidify(fragments, vram, width, height, pixels)
            9 -> GreyPathTracerTeam2.solidify(fragments, vram, width, height, pixels)
            else -> GreyPathTracerTeam2.solidify(fragments, vram, width, height, pixels)
        }
    }
}
