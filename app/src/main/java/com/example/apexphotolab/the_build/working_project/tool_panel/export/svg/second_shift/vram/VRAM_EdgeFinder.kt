package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram

import android.graphics.Point
import java.nio.ByteBuffer

/**
 * Job: VRAM Edge Finder.
 * Responsibility: Identifying perimeter pixels of a shape using VRAM bitmasks.
 */
object VRAM_EdgeFinder {

    /**
     * Standard bitmask edge finder. 
     * Now primarily used to find the "Fences" created by the 3D Geometric Map.
     */
    fun findEdgesVRAM(vram: ByteBuffer, width: Int, height: Int): HashSet<Point> {
        val edgePixels = HashSet<Point>()
        val offsets = arrayOf(
            Point(0, -1), Point(1, -1), Point(1, 0), Point(1, 1),
            Point(0, 1), Point(-1, 1), Point(-1, 0), Point(-1, -1)
        )

        for (y in 0 until height) {
            val rowOffset = y * width
            for (x in 0 until width) {
                val idx = rowOffset + x
                if (getBit(vram, idx)) {
                    var isEdge = false
                    for (offset in offsets) {
                        val nx = x + offset.x
                        val ny = y + offset.y
                        if (nx !in 0 until width || ny !in 0 until height) {
                            isEdge = true; break
                        }
                        if (!getBit(vram, ny * width + nx)) {
                            isEdge = true; break
                        }
                    }
                    if (isEdge) edgePixels.add(Point(x, y))
                }
            }
        }
        return edgePixels
    }

    private fun getBit(buffer: ByteBuffer, index: Int): Boolean {
        val byteIdx = index / 8
        if (byteIdx >= buffer.capacity()) return false
        val bitIdx = index % 8
        return (buffer.get(byteIdx).toInt() and (1 shl bitIdx)) != 0
    }
}
