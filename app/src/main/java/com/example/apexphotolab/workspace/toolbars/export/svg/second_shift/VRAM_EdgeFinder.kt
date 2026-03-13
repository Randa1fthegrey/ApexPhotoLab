package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift

import android.graphics.Point
import java.nio.ByteBuffer

/**
 * A specialized tool for the Second Shift.
 * High-performance edge finding using VRAM bitmasks to prevent heap-based OOM crashes.
 */
object VRAM_EdgeFinder {

    /**
     * Identifies perimeter pixels by scanning the VRAM bitmask.
     * @param vram The bitmask of the blob (populated by Converter/Sanitizer).
     * @return A HashSet of Point objects representing only the edges.
     */
    fun findEdgesVRAM(vram: ByteBuffer, width: Int, height: Int): HashSet<Point> {
        val edgePixels = HashSet<Point>()
        
        val offsets = arrayOf(
            Point(0, -1), Point(1, -1), Point(1, 0), Point(1, 1),
            Point(0, 1), Point(-1, 1), Point(-1, 0), Point(-1, -1)
        )

        // Scan the bitmask. We only iterate over indices that are actually set.
        for (y in 0 until height) {
            val rowOffset = y * width
            for (x in 0 until width) {
                val idx = rowOffset + x
                if (getBit(vram, idx)) {
                    var isEdge = false
                    for (offset in offsets) {
                        val nx = x + offset.x
                        val ny = y + offset.y

                        // 1. Boundary Check
                        if (nx !in 0 until width || ny !in 0 until height) {
                            isEdge = true
                            break
                        }

                        // 2. Neighbor Check (against bitmask)
                        if (!getBit(vram, ny * width + nx)) {
                            isEdge = true
                            break
                        }
                    }

                    if (isEdge) {
                        edgePixels.add(Point(x, y))
                    }
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
