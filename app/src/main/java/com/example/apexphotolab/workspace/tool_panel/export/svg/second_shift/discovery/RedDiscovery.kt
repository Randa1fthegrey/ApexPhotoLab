package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.discovery

import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.tracers.RedPathTracer
import java.nio.ByteBuffer

/**
 * Job #1 (Discovery Phase): The Red Team Scout.
 * Discovers raw fragments for the RED color group.
 */
object RedDiscovery {
    fun trace(edges: HashSet<Point>, vram: ByteBuffer, width: Int): List<List<Point>> {
        return RedPathTracer.trace(edges, vram, width)
    }
}
