package com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift

import android.graphics.Point
import com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.tracers.tracer_team2.*
import java.nio.ByteBuffer

/**
 * Job #2: The Maintenance Switchboard.
 * Takes raw fragments and routes them to the correct Maintenance Team (Team 2).
 * Updated: Restored pure color-based routing to support 14 individual border shapes.
 */
object MaintenanceRouter {

    /**
     * Routes fragments to specialized maintenance teams.
     */
    fun route(
        index: Int,
        fragments: List<List<Point>>,
        vram: ByteBuffer,
        width: Int,
        height: Int,
        pixels: IntArray
    ): List<List<Point>> {
        // Return the solidified paths directly. 
        // We no longer weld across color groups here to ensure 14 distinct border shapes.
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
