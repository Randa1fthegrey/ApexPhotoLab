package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure

import android.graphics.Point

/**
 * Job: Second Shift Result Merger.
 * Responsibility: Consolidating path lists from different tracing teams (Multicolor and Standard) into a single collection.
 */
object SecondShiftResultMerger {

    fun merge(team1Paths: List<List<Point>>, team2Paths: List<List<Point>>): List<List<Point>> {
        return team1Paths + team2Paths
    }
}