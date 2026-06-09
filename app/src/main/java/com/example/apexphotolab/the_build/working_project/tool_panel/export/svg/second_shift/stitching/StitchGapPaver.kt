package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.stitching

import android.graphics.Point

/**
 * Job: Stitch Gap Paver.
 * Responsibility: Generating a sequence of points to bridge a gap between two path endpoints.
 */
object StitchGapPaver {

    fun pave(path: MutableList<Point>, from: Point, to: Point) {
        var cx = from.x
        var cy = from.y
        while (cx != to.x || cy != to.y) {
            if (cx < to.x) cx++ else if (cx > to.x) cx--
            if (cy < to.y) cy++ else if (cy > to.y) cy--
            path.add(Point(cx, cy))
        }
    }
}