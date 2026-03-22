package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift

import android.graphics.Point

/**
 * Job #6: The Result Aggregator.
 * Responsible for collecting and merging fragmented results from all parallel jobs.
 */
object ResultAggregator {

    /**
     * Aggregates path fragments and edge sets from multiple tracing jobs.
     */
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
