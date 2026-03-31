package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.discovery

import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.tracers.CyanPathTracer
import java.nio.ByteBuffer

/**
 * Job #1 (Discovery Phase): The Cyan Team Scout.
 */
object CyanDiscovery {
    fun trace(edges: HashSet<Point>, vram: ByteBuffer, width: Int): List<List<Point>> {
        return CyanPathTracer.trace(edges, vram, width)
    }
}
