package com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift.consolidators

import android.graphics.Point
import android.graphics.Rect

import com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift.CensusReport

/**
 * Consolidation specialist for the YELLOW color group.
 */
object YellowConsolidator {

    private const val REACH_RADIUS = 10 

    fun consolidate(paths: List<List<Point>>, reports: List<CensusReport>): Pair<List<List<Point>>, List<CensusReport>> {
        if (paths.size <= 1) return Pair(paths, reports)

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
                if (isSpatiallyRelated(currentBounds, nextBounds, REACH_RADIUS)) {
                    currentPath.add(Point(-1, -1))
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
        var minX = Int.MAX_VALUE; var minY = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE; var maxY = Int.MIN_VALUE
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
