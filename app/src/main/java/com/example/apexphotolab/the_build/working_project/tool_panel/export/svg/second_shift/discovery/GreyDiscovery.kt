package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.discovery

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.GreyPathTracer
import java.nio.ByteBuffer

/**
 * Job: Grey Discovery Scout.
 * Responsibility: Discovering raw path fragments for the Grey color group.
 */
object GreyDiscovery {

    fun trace(
        edges: HashSet<Point>,
        vram: ByteBuffer,
        width: Int,
        sharedRemainingSet: MutableSet<Point>? = null,
        specificCandidates: List<Point>? = null
    ): List<List<Point>> {
        val candidates = specificCandidates ?: edges.sortedBy { it.y * 10000 + it.x }
        return GreyPathTracer.trace(candidates, vram, width, sharedRemainingSet)
    }
}
