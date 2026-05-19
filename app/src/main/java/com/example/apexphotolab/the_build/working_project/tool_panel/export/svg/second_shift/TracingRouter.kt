package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.discovery.*
import java.nio.ByteBuffer

/**
 * Job: Tracing Router.
 * Responsibility: Routing edges through the Discovery and Maintenance pipeline for a given color group.
 */
object TracingRouter {

    fun route(
        index: Int,
        edges: HashSet<Point>,
        vram: ByteBuffer,
        width: Int,
        height: Int,
        pixels: IntArray,
        sharedRemainingSet: MutableSet<Point>? = null,
        specificCandidates: List<Point>? = null
    ): List<List<Point>> {
        val fragments = when (index) {
            0 -> RedDiscovery.trace(edges, vram, width, sharedRemainingSet, specificCandidates)
            1 -> GreenDiscovery.trace(edges, vram, width, sharedRemainingSet, specificCandidates)
            2 -> BlueDiscovery.trace(edges, vram, width, sharedRemainingSet, specificCandidates)
            3 -> YellowDiscovery.trace(edges, vram, width, sharedRemainingSet, specificCandidates)
            4 -> CyanDiscovery.trace(edges, vram, width, sharedRemainingSet, specificCandidates)
            5 -> MagentaDiscovery.trace(edges, vram, width, sharedRemainingSet, specificCandidates)
            6 -> WhiteDiscovery.trace(edges, vram, width, sharedRemainingSet, specificCandidates)
            7 -> AlphaDiscovery.trace(edges, vram, width, sharedRemainingSet, specificCandidates)
            8 -> BlackDiscovery.trace(edges, vram, width, sharedRemainingSet, specificCandidates)
            9 -> GreyDiscovery.trace(edges, vram, width, sharedRemainingSet, specificCandidates)
            else -> GreyDiscovery.trace(edges, vram, width, sharedRemainingSet, specificCandidates)
        }

        return MaintenanceRouter.route(index, fragments, vram, width, height, pixels)
    }
}
