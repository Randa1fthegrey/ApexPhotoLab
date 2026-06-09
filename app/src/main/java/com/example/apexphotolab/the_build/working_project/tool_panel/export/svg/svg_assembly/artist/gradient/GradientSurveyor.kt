package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.gradient

import android.graphics.Point

/**
 * Job: Gradient Surveyor.
 * Responsibility: Calculating the bounding dimensions and dominant orientation axis of a geometric path.
 */
object GradientSurveyor {

    data class PathSurvey(
        val p1: Point,
        val p2: Point,
        val isHorizontal: Boolean
    )

    fun survey(points: List<Point>): PathSurvey? {
        if (points.isEmpty()) return null

        val minX = points.minOf { it.x }
        val maxX = points.maxOf { it.x }
        val minY = points.minOf { it.y }
        val maxY = points.maxOf { it.y }
        val w = maxX - minX
        val h = maxY - minY

        return if (w > h) {
            PathSurvey(Point(minX, (minY + maxY) / 2), Point(maxX, (minY + maxY) / 2), true)
        } else {
            PathSurvey(Point((minX + maxX) / 2, minY), Point((minX + maxX) / 2, maxY), false)
        }
    }
}