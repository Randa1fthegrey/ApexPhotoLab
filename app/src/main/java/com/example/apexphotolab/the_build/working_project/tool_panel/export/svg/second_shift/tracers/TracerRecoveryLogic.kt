package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers

import android.graphics.Point
import kotlin.math.abs

/**
 * Job: Tracer Recovery Logic.
 * Responsibility: Attempting to recover a path when the standard navigator hits a dead end by searching a wider radius.
 */
object TracerRecoveryLogic {

    fun findRecoveryPoint(current: Point, remaining: Set<Point>): Point? {
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
}