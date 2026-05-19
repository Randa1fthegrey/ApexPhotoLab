package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift

import android.graphics.Point

/**
 * Job: Result Aggregator.
 * Responsibility: Collecting and merging path fragments and edge sets from all parallel tracing jobs.
 */
object ResultAggregator {

    fun aggregate(
        results: List<Pair<List<List<Point>>, HashSet<Point>>>
    ): Pair<List<List<Point>>, HashSet<Point>> {
        val allPaths = mutableListOf<List<Point>>()
        val masterEdges = HashSet<Point>()

        results.forEach { (paths, edges) ->
            allPaths.addAll(paths)
            masterEdges.addAll(edges)
        }

        return Pair(allPaths, masterEdges)
    }
}
