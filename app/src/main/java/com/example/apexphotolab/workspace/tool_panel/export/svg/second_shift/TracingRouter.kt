package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift

import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.discovery.*
import java.nio.ByteBuffer

/**
 * Job #5: The Routing Specialist.
 * Acts as the master switchboard for the second shift.
 * Single Responsibility: Coordinates the handoff between Discovery and Maintenance.
 */
object TracingRouter {

    /**
     * Routes the edges and VRAM bitmask through the full second-shift pipeline.
     */
    fun route(
        index: Int, 
        edges: HashSet<Point>, 
        vram: ByteBuffer, 
        width: Int, 
        height: Int,
        pixels: IntArray
    ): List<List<Point>> {
        // 1. Job 1: Discovery (Delegated to specialized color scouts)
        val fragments = when (index) {
            0 -> RedDiscovery.trace(edges, vram, width)
            1 -> GreenDiscovery.trace(edges, vram, width)
            2 -> BlueDiscovery.trace(edges, vram, width)
            3 -> YellowDiscovery.trace(edges, vram, width)
            4 -> CyanDiscovery.trace(edges, vram, width)
            5 -> MagentaDiscovery.trace(edges, vram, width)
            6 -> WhiteDiscovery.trace(edges, vram, width)
            7 -> AlphaDiscovery.trace(edges, vram, width)
            8 -> BlackDiscovery.trace(edges, vram, width)
            9 -> GreyDiscovery.trace(edges, vram, width)
            else -> GreyDiscovery.trace(edges, vram, width)
        }

        // 2. Job 2: Maintenance (Delegated to the Maintenance Router)
        return MaintenanceRouter.route(index, fragments, vram, width, height, pixels)
    }
}
