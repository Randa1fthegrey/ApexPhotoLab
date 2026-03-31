package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.discovery

import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.tracers.YellowPathTracer
import java.nio.ByteBuffer

/**
 * Job #1 (Discovery Phase): The Yellow Team Scout.
 */
object YellowDiscovery {
    fun trace(edges: HashSet<Point>, vram: ByteBuffer, width: Int): List<List<Point>> {
        return YellowPathTracer.trace(edges, vram, width)
    }
}
