package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers

import android.graphics.Point

/**
 * Job: Multicolor Boundary Tracer (The Orchestrator).
 * Responsibility: Coordinating the path discovery for complex blobs by delegating navigation and recovery logic.
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

                var nextPoint = TracerNavigator.findNextStep(currentPoint, previousPoint, remaining, home, path.size)

                if (nextPoint == null) {
                    nextPoint = TracerRecoveryLogic.findRecoveryPoint(currentPoint, remaining)
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
                        val branch = TracerNavigator.findNextStep(junction, null, remaining, home, path.size)
                        if (branch != null) {
                            path.add(Point(-1, -1)) // Sentinel for branch break
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
}