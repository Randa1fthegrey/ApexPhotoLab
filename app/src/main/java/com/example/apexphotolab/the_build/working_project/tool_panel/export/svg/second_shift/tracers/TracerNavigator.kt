package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers

import android.graphics.Point

/**
 * Job: Tracer Navigator.
 * Responsibility: Determining the next optimal step for a path tracer based on 8-neighbor availability.
 */
object TracerNavigator {

    private val OFFSETS = arrayOf(
        Point(0, -1), Point(1, -1), Point(1, 0), Point(1, 1),
        Point(0, 1), Point(-1, 1), Point(-1, 0), Point(-1, -1)
    )

    fun findNextStep(
        current: Point,
        previous: Point?,
        remaining: Set<Point>,
        home: Point,
        pathSize: Int
    ): Point? {
        val lastMoveIndex = if (previous != null) {
            val dx = current.x - previous.x
            val dy = current.y - previous.y
            OFFSETS.indexOfFirst { it.x == dx && it.y == dy }
        } else -1

        val searchStartIndex = if (lastMoveIndex != -1) (lastMoveIndex + 5) % 8 else 0

        // Preferred: Cardinal directions first (even indices)
        for (i in 0 until 8) {
            val idx = (searchStartIndex + i) % 8
            if (idx % 2 != 0) continue
            val offset = OFFSETS[idx]
            val candidate = Point(current.x + offset.x, current.y + offset.y)
            if (candidate == home && pathSize > 10) return home
            if (remaining.contains(candidate)) return candidate
        }

        // Secondary: Diagonal directions
        for (i in 0 until 8) {
            val idx = (searchStartIndex + i) % 8
            if (idx % 2 == 0) continue
            val offset = OFFSETS[idx]
            val candidate = Point(current.x + offset.x, current.y + offset.y)
            if (candidate == home && pathSize > 10) return home
            if (remaining.contains(candidate)) return candidate
        }

        return null
    }
}