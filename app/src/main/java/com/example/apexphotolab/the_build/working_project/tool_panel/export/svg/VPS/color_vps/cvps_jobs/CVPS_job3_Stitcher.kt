package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.scouts.GradientIntelligenceAgency
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.scouts.GradientReport
import java.nio.ByteBuffer
import kotlin.math.abs

/**
 * Job: CVPS Job 3 Specialist - Stitcher.
 * Responsibility: Bridging gaps between path fragments and closing loops using a high-reach search.
 */
object CVPS_job3_Stitcher {

    fun execute(
        colorId: Int,
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
                    if (isNear(pathEnd, candidate.first(), val_util.STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = false; addToStart = false; break
                    }
                    if (isNear(pathEnd, candidate.last(), val_util.STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = true; addToStart = false; break
                    }
                    if (isNear(pathStart, candidate.last(), val_util.STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = false; addToStart = true; break
                    }
                    if (isNear(pathStart, candidate.first(), val_util.STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = true; addToStart = true; break
                    }
                }

                if (matchIndex != -1) {
                    val candidate = pool.removeAt(matchIndex)
                    if (reverseCandidate) candidate.reverse()

                    if (isPaveLegal(if (addToStart) candidate.last() else currentPath.last(),
                                   if (addToStart) currentPath.first() else candidate.first(),
                                   vram, width, height)) {
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

            if (currentPath.size >= val_util.MIN_PATH_SIZE) {
                val start = currentPath.first()
                val end = currentPath.last()
                
                // Closure check
                if (isNear(end, start, val_util.CLOSURE_RADIUS) && isPaveLegal(end, start, vram, width, height)) {
                    if (start.x != end.x || start.y != end.y) {
                        paveGap(currentPath, end, start)
                    }
                }

                // Border Gradient Recording
                if (isPathOnBorder(currentPath, width, height)) {
                    val startColor = pixels[start.y * width + start.x]
                    val endColor = pixels[end.y * width + end.x]
                    GradientIntelligenceAgency.record(GradientReport(colorId, currentPath, startColor, endColor, true))
                }

                stitchedPaths.add(currentPath)
            }
        }

        return stitchedPaths
    }

    private fun isNear(p1: Point, p2: Point, radius: Int): Boolean = abs(p1.x - p2.x) <= radius && abs(p1.y - p2.y) <= radius

    private fun isPaveLegal(from: Point, to: Point, vram: ByteBuffer, width: Int, height: Int): Boolean {
        var cx = from.x; var cy = from.y
        while (cx != to.x || cy != to.y) {
            if (cx < to.x) cx++ else if (cx > to.x) cx--
            if (cy < to.y) cy++ else if (cy > to.y) cy--

            var foundEdge = false
            for (dy in -1..1) {
                for (dx in -1..1) {
                    val nx = cx + dx; val ny = cy + dy
                    if (nx in 0 until width && ny in 0 until height && CVPS_VRAM_Util.getBit(vram, ny * width + nx)) {
                        foundEdge = true; break
                    }
                }
                if (foundEdge) break
            }
            if (!foundEdge) return false
        }
        return true
    }

    private fun isPathOnBorder(path: List<Point>, w: Int, h: Int): Boolean {
        return path.any { it.x != -1 && (it.x < val_util.BORDER_BUFFER || it.x > w - val_util.BORDER_BUFFER || it.y < val_util.BORDER_BUFFER || it.y > h - val_util.BORDER_BUFFER) }
    }

    private fun paveGap(path: MutableList<Point>, from: Point, to: Point) {
        var cx = from.x; var cy = from.y
        while (cx != to.x || cy != to.y) {
            if (cx < to.x) cx++ else if (cx > to.x) cx--
            if (cy < to.y) cy++ else if (cy > to.y) cy--
            path.add(Point(cx, cy))
        }
    }
}
