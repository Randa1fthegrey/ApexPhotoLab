package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import android.graphics.Rect
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusReport

/**
 * Job: CVPS Job 3 Specialist - Welder.
 * Responsibility: Grouping spatially related paths into single SVG elements using MoveTo (M) commands.
 */
object CVPS_job3_Welder {

    fun execute(
        colorId: Int,
        paths: List<List<Point>>,
        reports: List<CensusReport>
    ): Pair<List<List<Point>>, List<CensusReport>> {
        if (paths.size <= 1) return Pair(paths, reports)

        val reachRadius = when (colorId) {
            0, 1, 2, 3, 4, 5 -> val_util.REACH_RADIUS_DEFAULT
            6 -> val_util.REACH_RADIUS_WHITE
            7 -> val_util.REACH_RADIUS_ALPHA
            8 -> val_util.REACH_RADIUS_BLACK
            9 -> val_util.REACH_RADIUS_GREY
            else -> val_util.REACH_RADIUS_DEFAULT
        }

        val consolidatedPaths = mutableListOf<List<Point>>()
        val consolidatedReports = mutableListOf<CensusReport>()
        val usedIndices = mutableSetOf<Int>()

        for (i in paths.indices) {
            if (i in usedIndices) continue

            val currentPath = paths[i].toMutableList()
            val currentReport = if (i < reports.size) reports[i] else CensusReport(colorId, 0, 0, 0.0f, 0)
            val currentBounds = getBoundingBox(paths[i])
            usedIndices.add(i)

            for (j in i + 1 until paths.size) {
                if (j in usedIndices) continue

                val nextBounds = getBoundingBox(paths[j])
                if (isSpatiallyRelated(currentBounds, nextBounds, reachRadius)) {
                    currentPath.add(val_util.SENTINEL)
                    currentPath.addAll(paths[j])
                    usedIndices.add(j)
                }
            }

            consolidatedPaths.add(currentPath)
            consolidatedReports.add(currentReport)
        }

        return Pair(consolidatedPaths, consolidatedReports)
    }

    private fun getBoundingBox(path: List<Point>): Rect {
        var minX = Int.MAX_VALUE; var minY = Int.MAX_VALUE; var maxX = Int.MIN_VALUE; var maxY = Int.MIN_VALUE
        for (p in path) {
            if (p.x == -1) continue
            if (p.x < minX) minX = p.x; if (p.x > maxX) maxX = p.x
            if (p.y < minY) minY = p.y; if (p.y > maxY) maxY = p.y
        }
        return Rect(minX, minY, maxX, maxY)
    }

    private fun isSpatiallyRelated(r1: Rect, r2: Rect, buffer: Int): Boolean {
        val er1 = Rect(r1.left - buffer, r1.top - buffer, r1.right + buffer, r1.bottom + buffer)
        return er1.intersects(r2.left, r2.top, r2.right, r2.bottom)
    }
}
