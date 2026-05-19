package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.discovery

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.GreenPathTracer
import java.nio.ByteBuffer

/**
 * Job: Green Discovery Scout.
 * Responsibility: Discovering raw path fragments for the Green color group.
 */
object GreenDiscovery {

    fun trace(
        edges: HashSet<Point>,
        vram: ByteBuffer,
        width: Int,
        sharedRemainingSet: MutableSet<Point>? = null,
        specificCandidates: List<Point>? = null
    ): List<List<Point>> {
        val candidates = specificCandidates ?: edges.sortedBy { it.y * 10000 + it.x }
        return GreenPathTracer.trace(candidates, vram, width, sharedRemainingSet)
    }
}
