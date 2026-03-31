package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.discovery

import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.tracers.AlphaPathTracer
import java.nio.ByteBuffer

/**
 * Job #1 (Discovery Phase): The Alpha Team Scout.
 */
object AlphaDiscovery {
    fun trace(edges: HashSet<Point>, vram: ByteBuffer, width: Int): List<List<Point>> {
        return AlphaPathTracer.trace(edges, vram, width)
    }
}
