package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.discovery

import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.tracers.BlackPathTracer
import java.nio.ByteBuffer

/**
 * Job #1 (Discovery Phase): The Black Team Scout.
 */
object BlackDiscovery {
    fun trace(edges: HashSet<Point>, vram: ByteBuffer, width: Int): List<List<Point>> {
        return BlackPathTracer.trace(edges, vram, width)
    }
}
