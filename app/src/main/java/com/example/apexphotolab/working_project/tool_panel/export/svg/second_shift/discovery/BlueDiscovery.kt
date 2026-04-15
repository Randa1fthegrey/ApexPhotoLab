package com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.discovery

import android.graphics.Point
import com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.tracers.BluePathTracer
import java.nio.ByteBuffer

/**
 * Job #1 (Discovery Phase): The Blue Team Scout.
 */
object BlueDiscovery {
    fun trace(edges: HashSet<Point>, vram: ByteBuffer, width: Int): List<List<Point>> {
        return BluePathTracer.trace(edges, vram, width)
    }
}
