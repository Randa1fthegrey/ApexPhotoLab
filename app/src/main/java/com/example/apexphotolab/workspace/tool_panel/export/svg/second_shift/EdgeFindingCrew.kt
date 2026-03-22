package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift

import android.graphics.Point
import java.nio.ByteBuffer

/**
 * Job #4: The Edge Finding Crew.
 * Manages the handoff to the VRAM-optimized EdgeFinder.
 */
object EdgeFindingCrew {

    /**
     * Finds the boundary pixels using VRAM to prevent memory issues.
     */
    fun findEdges(blob: HashSet<Point>, width: Int, height: Int, vram: ByteBuffer): HashSet<Point> {
        return VRAM_EdgeFinder.findEdgesVRAM(vram, width, height)
    }
}
