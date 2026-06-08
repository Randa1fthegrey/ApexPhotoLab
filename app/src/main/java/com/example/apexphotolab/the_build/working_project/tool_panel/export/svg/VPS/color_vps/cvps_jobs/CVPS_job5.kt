package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import android.graphics.Rect
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.CensusReport

/**
 * Job: CVPS Job 5 - Consolidators.
 * Responsibility: Merging spatially related paths into unified structures via sentinel welding.
 */
object CVPS_job5 {

    data class ConsolidationData(
        val paths: List<List<Point>>,
        val reports: List<CensusReport>,
        var result: Pair<List<List<Point>>, List<CensusReport>>? = null
    )

    fun execute(colorId: Int, data: Any?) {
        val cData = data as? ConsolidationData ?: return
        val paths = cData.paths
        val reports = cData.reports

        if (paths.size <= 1) {
            cData.result = Pair(paths, reports)
            return
        }

        val reachRadius = when (colorId) {
            0, 1, 2, 3, 4, 5 -> 10 // Red, Green, Blue, Yellow, Cyan, Magenta
            6 -> 5  // White
            7 -> 15 // Alpha
            8 -> 20 // Black
            9 -> 15 // Grey
            else -> 10
        }

        val consolidatedPaths = mutableListOf<List<Point>>()
        val consolidatedReports = mutableListOf<CensusReport>()
        val usedIndices = mutableSetOf<Int>()

        for (i in paths.indices) {
            if (i in usedIndices) continue

            val currentPath = paths[i].toMutableList()
            val currentReport = reports[i]
            val currentBounds = getBoundingBox(paths[i])
            usedIndices.add(i)

            for (j in i + 1 until paths.size) {
                if (j in usedIndices) continue

                val nextBounds = getBoundingBox(paths[j])
                if (isSpatiallyRelated(currentBounds, nextBounds, reachRadius)) {
                    currentPath.add(Point(-1, -1))
                    currentPath.addAll(paths[j])
                    usedIndices.add(j)
                }
            }

            consolidatedPaths.add(currentPath)
            consolidatedReports.add(currentReport)
        }

        cData.result = Pair(consolidatedPaths, consolidatedReports)
    }

    private fun getBoundingBox(path: List<Point>): Rect {
        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var maxY = Int.MIN_VALUE
        for (p in path) {
            if (p.x == -1) continue
            if (p.x < minX) minX = p.x
            if (p.x > maxX) maxX = p.x
            if (p.y < minY) minY = p.y
            if (p.y > maxY) maxY = p.y
        }
        return Rect(minX, minY, maxX, maxY)
    }

    private fun isSpatiallyRelated(r1: Rect, r2: Rect, buffer: Int): Boolean {
        val er1 = Rect(r1.left - buffer, r1.top - buffer, r1.right + buffer, r1.bottom + buffer)
        return er1.intersects(r2.left, r2.top, r2.right, r2.bottom)
    }
}