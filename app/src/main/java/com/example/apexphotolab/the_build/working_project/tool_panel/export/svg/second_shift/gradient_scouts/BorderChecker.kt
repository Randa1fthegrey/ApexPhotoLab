package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Point

/**
 * Job: Border Checker.
 * Responsibility: Determining whether a path falls within the border buffer zone of the canvas.
 */
object BorderChecker {

    fun isOnBorder(path: List<Point>, width: Int, height: Int): Boolean {
        return path.any {
            it.x < BORDER_BUFFER ||
            it.x > width - BORDER_BUFFER ||
            it.y < BORDER_BUFFER ||
            it.y > height - BORDER_BUFFER
        }
    }

    private const val BORDER_BUFFER = 50
}
