package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.tracers.tracer_team2

import android.graphics.Point
import java.nio.ByteBuffer
import kotlin.math.abs

/**
 * Team 2 (Maintenance Crew) for BLACK shapes.
 * Updated: Structural specialist. Builds watertight blocks using 
 * pre-populated intelligence from the Gradient Scouts.
 */
object BlackPathTracerTeam2 {

    private const val STITCH_RADIUS = 25
    private const val CLOSURE_RADIUS = 100
    private const val MIN_PATH_SIZE = 10 

    fun solidify(
        fragments: List<List<Point>>,
        vram: ByteBuffer,
        width: Int,
        height: Int,
        pixels: IntArray
    ): List<List<Point>> {
        if (fragments.isEmpty()) return emptyList()

        val stitchedPaths = mutableListOf<MutableList<Point>>()
        val pool = fragments.map { it.toMutableList() }.toMutableList()

        while (pool.isNotEmpty()) {
            val currentPath = pool.removeAt(0)
            var changed = true

            while (changed) {
                changed = false
                val pathStart = currentPath.first()
                val pathEnd = currentPath.last()

                var matchIndex = -1
                var reverseCandidate = false
                var addToStart = false

                for (i in pool.indices) {
                    val candidate = pool[i]
                    if (isNear(pathEnd, candidate.first(), STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = false; addToStart = false; break
                    }
                    if (isNear(pathEnd, candidate.last(), STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = true; addToStart = false; break
                    }
                    if (isNear(pathStart, candidate.last(), STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = false; addToStart = true; break
                    }
                    if (isNear(pathStart, candidate.first(), STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = true; addToStart = true; break
                    }
                }

                if (matchIndex != -1) {
                    val candidate = pool.removeAt(matchIndex)
                    if (reverseCandidate) candidate.reverse()
                    
                    if (isPaveLegal(if (addToStart) candidate.last() else currentPath.last(), 
                                   if (addToStart) currentPath.first() else candidate.first(), 
                                   vram, width)) {
                        if (addToStart) {
                            paveGap(candidate, candidate.last(), currentPath.first())
                            currentPath.addAll(0, candidate)
                        } else {
                            paveGap(currentPath, currentPath.last(), candidate.first())
                            currentPath.addAll(candidate)
                        }
                        changed = true
                    } else {
                        pool.add(candidate)
                    }
                }
            }
            
            if (currentPath.size >= MIN_PATH_SIZE) {
                val start = currentPath.first()
                val end = currentPath.last()
                if (isNear(end, start, CLOSURE_RADIUS) && isPaveLegal(end, start, vram, width)) {
                    if (start.x != end.x || start.y != end.y) {
                        paveGap(currentPath, end, start)
                    }
                }
                stitchedPaths.add(currentPath)
            }
        }

        return stitchedPaths
    }

    private fun isNear(p1: Point, p2: Point, radius: Int): Boolean {
        return abs(p1.x - p2.x) <= radius && abs(p1.y - p2.y) <= radius
    }

    private fun isPaveLegal(from: Point, to: Point, vram: ByteBuffer, width: Int): Boolean {
        var cx = from.x; var cy = from.y
        while (cx != to.x || cy != to.y) {
            if (cx < to.x) cx++ else if (cx > to.x) cx--
            if (cy < to.y) cy++ else if (cy > to.y) cy--
            if (!getBit(vram, cy * width + cx)) return false
        }
        return true
    }

    private fun paveGap(path: MutableList<Point>, from: Point, to: Point) {
        var cx = from.x; var cy = from.y
        while (cx != to.x || cy != to.y) {
            if (cx < to.x) cx++ else if (cx > to.x) cx--
            if (cy < to.y) cy++ else if (cy > to.y) cy--
            path.add(Point(cx, cy))
        }
    }

    private fun getBit(buffer: ByteBuffer, index: Int): Boolean {
        val byteIdx = index / 8
        if (byteIdx >= buffer.capacity()) return false
        val bitIdx = index % 8
        return (buffer.get(byteIdx).toInt() and (1 shl bitIdx)) != 0
    }
}
