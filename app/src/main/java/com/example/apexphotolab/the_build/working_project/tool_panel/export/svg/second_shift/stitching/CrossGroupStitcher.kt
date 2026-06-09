package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.stitching

import android.graphics.Point

/**
 * Job: Cross-Group Stitcher (The Orchestrator).
 * Responsibility: Coordinating the closure of path fragments by delegating proximity matching and gap paving logic.
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
                if (StitchProximityMatcher.isNear(start, end, ALREADY_CLOSED_RADIUS)) break

                var matchIndex = -1
                var reverseCandidate = false
                var addToStart = false

                for (i in pool.indices) {
                    val candidate = pool[i]
                    if (candidate.size < MIN_FRAGMENT_SIZE) continue

                    if (StitchProximityMatcher.isNear(end, candidate.first(), CROSS_STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = false; addToStart = false; break
                    }
                    if (StitchProximityMatcher.isNear(end, candidate.last(), CROSS_STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = true; addToStart = false; break
                    }
                    if (StitchProximityMatcher.isNear(start, candidate.last(), CROSS_STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = false; addToStart = true; break
                    }
                    if (StitchProximityMatcher.isNear(start, candidate.first(), CROSS_STITCH_RADIUS)) {
                        matchIndex = i; reverseCandidate = true; addToStart = true; break
                    }
                }

                if (matchIndex != -1) {
                    val candidate = pool.removeAt(matchIndex)
                    if (reverseCandidate) candidate.reverse()

                    if (addToStart) {
                        StitchGapPaver.pave(candidate, candidate.last(), current.first())
                        current.addAll(0, candidate)
                    } else {
                        StitchGapPaver.pave(current, current.last(), candidate.first())
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

    private const val CROSS_STITCH_RADIUS = 35
    private const val ALREADY_CLOSED_RADIUS = 30
    private const val MIN_FRAGMENT_SIZE = 5
}