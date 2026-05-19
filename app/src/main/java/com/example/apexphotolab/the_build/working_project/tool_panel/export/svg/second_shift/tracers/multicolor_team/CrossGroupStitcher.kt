package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.multicolor_team

import android.graphics.Point
import kotlin.math.abs

/**
 * Job: Cross-Group Stitcher.
 * Responsibility: Connecting open path fragments whose endpoints fall at color group boundaries, bridging the spatial gaps that per-group Team 2 stitching cannot cross.
 */
object CrossGroupStitcher {

    fun stitch(allPaths: List<List<Point>>): List<List<Point>> {
        if (allPaths.size < 2) return allPaths

        val pool = allPaths.map { it.toMutableList() }.toMutableList()
        val completed = mutableListOf<List<Point>>()

        while (pool.isNotEmpty()) {
            val current = pool.removeAt(0)
            var changed = true

            while (changed) {
                changed = false

                val start = current.first()
                val end = current.last()

                // Path is already closed — no stitching needed
                if (isNear(start, end, ALREADY_CLOSED_RADIUS)) break

                var matchIndex = -1
                var reverseCandidate = false
                var addToStart = false

                for (i in pool.indices) {
                    val candidate = pool[i]
                    if (candidate.size < MIN_FRAGMENT_SIZE) continue

                    if (isNear(end, candidate.first(), CROSS_STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = false; addToStart = false; break
                    }
                    if (isNear(end, candidate.last(), CROSS_STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = true; addToStart = false; break
                    }
                    if (isNear(start, candidate.last(), CROSS_STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = false; addToStart = true; break
                    }
                    if (isNear(start, candidate.first(), CROSS_STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = true; addToStart = true; break
                    }
                }

                if (matchIndex != -1) {
                    val candidate = pool.removeAt(matchIndex)
                    if (reverseCandidate) candidate.reverse()

                    if (addToStart) {
                        paveGap(candidate, candidate.last(), current.first())
                        current.addAll(0, candidate)
                    } else {
                        paveGap(current, current.last(), candidate.first())
                        current.addAll(candidate)
                    }
                    changed = true
                }
            }

            if (current.size >= MIN_FRAGMENT_SIZE) {
                completed.add(current)
            }
        }

        return completed
    }

    private fun isNear(p1: Point, p2: Point, radius: Int): Boolean {
        return abs(p1.x - p2.x) <= radius && abs(p1.y - p2.y) <= radius
    }

    private fun paveGap(path: MutableList<Point>, from: Point, to: Point) {
        var cx = from.x
        var cy = from.y
        while (cx != to.x || cy != to.y) {
            if (cx < to.x) cx++ else if (cx > to.x) cx--
            if (cy < to.y) cy++ else if (cy > to.y) cy--
            path.add(Point(cx, cy))
        }
    }

    private const val CROSS_STITCH_RADIUS = 35
    private const val ALREADY_CLOSED_RADIUS = 30
    private const val MIN_FRAGMENT_SIZE = 5
}