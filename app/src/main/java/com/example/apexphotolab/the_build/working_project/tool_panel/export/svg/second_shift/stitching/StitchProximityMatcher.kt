package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.stitching

import android.graphics.Point
import kotlin.math.abs

/**
 * Job: Stitch Proximity Matcher.
 * Responsibility: Determining if two path endpoints are spatially close enough to be bridged.
 */
object StitchProximityMatcher {

    fun isNear(p1: Point, p2: Point, radius: Int): Boolean {
        return abs(p1.x - p2.x) <= radius && abs(p1.y - p2.y) <= radius
    }
}