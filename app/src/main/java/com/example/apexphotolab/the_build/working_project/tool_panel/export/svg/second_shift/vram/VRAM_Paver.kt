package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram

import android.graphics.Point
import java.nio.ByteBuffer
import kotlin.math.abs

/**
 * Job: VRAM Paver.
 * Responsibility: Marking specific paths as "Golden Rails" in the VRAM bitmask
 * to ensure tracers have a solid, unbroken path to follow.
 */
object VRAM_Paver {

    fun pave(path: List<Point>, vram: ByteBuffer, width: Int, height: Int) {
        if (path.size < 2) return

        for (i in 0 until path.size - 1) {
            val start = path[i]
            val end = path[i + 1]

            // Skip sentinels
            if (start.x == -1 || end.x == -1) continue

            interpolateAndMark(start, end, vram, width, height)
        }
    }

    private fun interpolateAndMark(p1: Point, p2: Point, vram: ByteBuffer, width: Int, height: Int) {
        var x = p1.x
        var y = p1.y
        val dx = abs(p2.x - p1.x)
        val dy = abs(p2.y - p1.y)
        val sx = if (p1.x < p2.x) 1 else -1
        val sy = if (p1.y < p2.y) 1 else -1
        var err = dx - dy

        while (true) {
            markBit(vram, x, y, width, height)

            if (x == p2.x && y == p2.y) break

            val e2 = 2 * err
            if (e2 > -dy) {
                err -= dy
                x += sx
            }
            if (e2 < dx) {
                err += dx
                y += sy
            }
        }
    }

    private fun markBit(vram: ByteBuffer, x: Int, y: Int, width: Int, height: Int) {
        if (x !in 0 until width || y !in 0 until height) return

        val index = y * width + x
        val byteIdx = index / 8
        if (byteIdx < vram.capacity()) {
            val bitIdx = index % 8
            val currentByte = vram.get(byteIdx).toInt()
            vram.put(byteIdx, (currentByte or (1 shl bitIdx)).toByte())
        }
    }
}
