package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.tracers.tracer_team2

import android.graphics.Point
import java.nio.ByteBuffer
import kotlin.math.abs

/**
 * Team 2 (Maintenance Crew) for YELLOW shapes.
 */
object YellowPathTracerTeam2 {

    fun solidify(
        fragments: List<List<Point>>,
        vram: ByteBuffer,
        width: Int
    ): List<List<Point>> {
        if (fragments.isEmpty()) return emptyList()

        val stitchedPaths = mutableListOf<MutableList<Point>>()
        val pool = fragments.map { it.toMutableList() }.toMutableList()

        while (pool.isNotEmpty()) {
            val currentPath = pool.removeAt(0)
            var changed = true

            while (changed) {
                changed = false
                val end = currentPath.last()

                var matchIndex = -1
                for (i in pool.indices) {
                    val candidate = pool[i]
                    val start = candidate.first()

                    if (isNear(end, start)) {
                        matchIndex = i
                        break
                    }
                }

                if (matchIndex != -1) {
                    val candidate = pool.removeAt(matchIndex)
                    val start = candidate.first()
                    paveGap(currentPath, end, start, vram, width)
                    currentPath.addAll(candidate)
                    changed = true
                }
            }
            
            if (currentPath.size > 2) {
                stitchedPaths.add(currentPath)
            }
        }

        return stitchedPaths
    }

    private fun isNear(p1: Point, p2: Point): Boolean {
        return abs(p1.x - p2.x) <= 3 && abs(p1.y - p2.y) <= 3
    }

    private fun paveGap(path: MutableList<Point>, from: Point, to: Point, vram: ByteBuffer, width: Int) {
        var cx = from.x
        var cy = from.y
        while (cx != to.x || cy != to.y) {
            if (cx < to.x) cx++ else if (cx > to.x) cx--
            if (cy < to.y) cy++ else if (cy > to.y) cy--
            val p = Point(cx, cy)
            path.add(p)
            setBit(vram, cy * width + cx)
        }
    }

    private fun setBit(buffer: ByteBuffer, index: Int) {
        val byteIdx = index / 8
        if (byteIdx >= buffer.capacity()) return
        val bitIdx = index % 8
        buffer.put(byteIdx, (buffer.get(byteIdx).toInt() or (1 shl bitIdx)).toByte())
    }
}
