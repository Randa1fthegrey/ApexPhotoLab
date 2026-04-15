package com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.discovery

import android.graphics.Point
import com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.tracers.GreenPathTracer
import java.nio.ByteBuffer

/**
 * Job #1 (Discovery Phase): The Green Team Scout.
 */
object GreenDiscovery {
    fun trace(edges: HashSet<Point>, vram: ByteBuffer, width: Int): List<List<Point>> {
        return GreenPathTracer.trace(edges, vram, width)
    }
}
