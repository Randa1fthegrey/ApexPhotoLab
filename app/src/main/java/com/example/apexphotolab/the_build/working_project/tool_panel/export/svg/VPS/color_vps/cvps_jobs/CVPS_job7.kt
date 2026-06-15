package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.scouts.GradientIntelligenceAgency
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.scouts.GradientReport
import java.nio.ByteBuffer
import kotlin.math.abs

/**
 * Job: CVPS Job 7 - Solidification (Team 2 Stitching).
 * Responsibility: Stitching path fragments into watertight geometric blocks and recording border gradients.
 */
object CVPS_job7 {

    data class SolidificationData(
        val fragments: List<List<Point>>,
        val vram: ByteBuffer,
        val width: Int,
        val height: Int,
        val pixels: IntArray,
        var result: List<List<Point>> = emptyList()
    )

    fun execute(colorId: Int, data: Any?) {
        val sData = data as? SolidificationData ?: return
        
        // UNIFIED STITCHING LOGIC: Replaces 10 legacy Team 2 files
        sData.result = solidify(colorId, sData.fragments, sData.vram, sData.width, sData.height, sData.pixels)
    }

    private fun solidify(
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
        // If near border, bypass legality check (allow perimeter closure)
        if (from.x < val_util.BORDER_BUFFER || from.x > width - val_util.BORDER_BUFFER || from.y < val_util.BORDER_BUFFER || from.y > height - val_util.BORDER_BUFFER ||
            to.x < val_util.BORDER_BUFFER || to.x > width - val_util.BORDER_BUFFER || to.y < val_util.BORDER_BUFFER || to.y > height - val_util.BORDER_BUFFER) return true

        var cx = from.x; var cy = from.y
        while (cx != to.x || cy != to.y) {
            if (cx < to.x) cx++ else if (cx > to.x) cx--
            if (cy < to.y) cy++ else if (cy > to.y) cy--

            // 2-pixel choke for internal shapes
            for (dy in -2..2) {
                for (dx in -2..2) {
                    val nx = cx + dx; val ny = cy + dy
                    if (nx !in 0 until width || ny !in 0 until height) return false
                    if (!getBit(vram, ny * width + nx)) return false
                }
            }
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

    private fun getBit(buffer: ByteBuffer, index: Int): Boolean {
        val byteIdx = index / 8
        if (byteIdx >= buffer.capacity()) return false
        val bitIdx = index % 8
        return (buffer.get(byteIdx).toInt() and (1 shl bitIdx)) != 0
    }
}
