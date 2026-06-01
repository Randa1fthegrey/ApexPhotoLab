package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.multicolor_team

import android.graphics.Point
import kotlin.math.abs

/**
 * Job: Multicolor Boundary Tracer.
 * Responsibility: Walking the outer edge of a multi-color blob to produce a single unified boundary path, ignoring internal color group boundaries.
 */
object MulticolorBoundaryTracer {

    fun trace(blob: MulticolorBlob): List<List<Point>> {
        if (blob.edgePoints.isEmpty()) return emptyList()

        val allPaths = mutableListOf<List<Point>>()
        val remaining = blob.edgePoints.toMutableSet()
        val homeCandidates = blob.edgePoints.sortedBy { it.y * 10000 + it.x }

        for (home in homeCandidates) {
            if (!remaining.contains(home)) continue

            val path = mutableListOf<Point>()
            val stack = mutableListOf<Point>()
            var currentPoint = home
            var previousPoint: Point? = null

            while (true) {
                path.add(currentPoint)
                remaining.remove(currentPoint)
                stack.add(currentPoint)

                var nextPoint = findNextStep(currentPoint, previousPoint, remaining, home, path.size)

                if (nextPoint == null) {
                    nextPoint = findRecoveryPoint(currentPoint, remaining)
                }

                if (nextPoint != null) {
                    if (nextPoint == home && path.size > 10) {
                        allPaths.add(path)
                        break
                    }
                    previousPoint = currentPoint
                    currentPoint = nextPoint
                } else {
                    var foundBranch = false
                    if (stack.isNotEmpty()) stack.removeAt(stack.size - 1)

                    while (stack.isNotEmpty()) {
                        val junction = stack.removeAt(stack.size - 1)
                        val branch = findNextStep(junction, null, remaining, home, path.size)
                        if (branch != null) {
                            path.add(Point(-1, -1))
                            previousPoint = junction
                            currentPoint = branch
                            foundBranch = true
                            break
                        }
                    }

                    if (!foundBranch) {
                        if (path.size > 2) allPaths.add(path)
                        break
                    }
                }
            }
        }

        return allPaths
    }

    private fun findNextStep(
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

        for (i in 0 until 8) {
            val idx = (searchStartIndex + i) % 8
            if (idx % 2 != 0) continue
            val offset = OFFSETS[idx]
            val candidate = Point(current.x + offset.x, current.y + offset.y)
            if (candidate == home && pathSize > 10) return home
            if (remaining.contains(candidate)) return candidate
        }

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

    private fun findRecoveryPoint(current: Point, remaining: Set<Point>): Point? {
        for (r in 1..3) {
            for (dy in -r..r) {
                for (dx in -r..r) {
                    if (abs(dx) < r && abs(dy) < r) continue
                    val candidate = Point(current.x + dx, current.y + dy)
                    if (remaining.contains(candidate)) return candidate
                }
            }
        }
        return null
    }

    private val OFFSETS = arrayOf(
        Point(0, -1),
        Point(1, -1),
        Point(1, 0),
        Point(1, 1),
        Point(0, 1),
        Point(-1, 1),
        Point(-1, 0),
        Point(-1, -1)
    )
}