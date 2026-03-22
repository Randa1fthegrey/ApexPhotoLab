package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift

import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.tracers.*
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.tracers.tracer_team2.*
import java.nio.ByteBuffer

/**
 * Job #5: The Routing Specialist.
 * Acts as the switchboard to route work to the correct color-specialist tracer.
 * Updated: Now coordinates the handoff from Team 1 (Discovery) to Team 2 (Maintenance).
 */
object TracingRouter {

    /**
     * Routes the edges and VRAM bitmask to the specialized tracer team.
     */
    fun route(index: Int, edges: HashSet<Point>, vram: ByteBuffer, width: Int): List<List<Point>> {
        // 1. Team 1: Discovery (Find raw fragments)
        val fragments = when (index) {
            0 -> RedPathTracer.trace(edges, vram, width)
            1 -> GreenPathTracer.trace(edges, vram, width)
            2 -> BluePathTracer.trace(edges, vram, width)
            3 -> YellowPathTracer.trace(edges, vram, width)
            4 -> CyanPathTracer.trace(edges, vram, width)
            5 -> MagentaPathTracer.trace(edges, vram, width)
            6 -> WhitePathTracer.trace(edges, vram, width)
            7 -> AlphaPathTracer.trace(edges, vram, width)
            8 -> BlackPathTracer.trace(edges, vram, width)
            9 -> GreyPathTracer.trace(edges, vram, width)
            else -> GreyPathTracer.trace(edges, vram, width)
        }

        // 2. Team 2: Maintenance (Stitch and solidify)
        return when (index) {
            0 -> RedPathTracerTeam2.solidify(fragments, vram, width)
            1 -> GreenPathTracerTeam2.solidify(fragments, vram, width)
            2 -> BluePathTracerTeam2.solidify(fragments, vram, width)
            3 -> YellowPathTracerTeam2.solidify(fragments, vram, width)
            4 -> CyanPathTracerTeam2.solidify(fragments, vram, width)
            5 -> MagentaPathTracerTeam2.solidify(fragments, vram, width)
            6 -> WhitePathTracerTeam2.solidify(fragments, vram, width)
            7 -> AlphaPathTracerTeam2.solidify(fragments, vram, width)
            8 -> BlackPathTracerTeam2.solidify(fragments, vram, width)
            9 -> GreyPathTracerTeam2.solidify(fragments, vram, width)
            else -> GreyPathTracerTeam2.solidify(fragments, vram, width)
        }
    }
}
