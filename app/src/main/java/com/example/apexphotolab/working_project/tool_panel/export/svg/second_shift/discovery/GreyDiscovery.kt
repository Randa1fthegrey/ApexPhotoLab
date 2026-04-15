package com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.discovery

import android.graphics.Point
import com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.tracers.GreyPathTracer
import java.nio.ByteBuffer

/**
 * Job #1 (Discovery Phase): The Grey Team Scout.
 */
object GreyDiscovery {
    fun trace(edges: HashSet<Point>, vram: ByteBuffer, width: Int): List<List<Point>> {
        return GreyPathTracer.trace(edges, vram, width)
    }
}
