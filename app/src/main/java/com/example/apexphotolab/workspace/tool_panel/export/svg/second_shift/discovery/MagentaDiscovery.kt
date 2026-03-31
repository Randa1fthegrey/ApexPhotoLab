package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.discovery

import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.tracers.MagentaPathTracer
import java.nio.ByteBuffer

/**
 * Job #1 (Discovery Phase): The Magenta Team Scout.
 */
object MagentaDiscovery {
    fun trace(edges: HashSet<Point>, vram: ByteBuffer, width: Int): List<List<Point>> {
        return MagentaPathTracer.trace(edges, vram, width)
    }
}
