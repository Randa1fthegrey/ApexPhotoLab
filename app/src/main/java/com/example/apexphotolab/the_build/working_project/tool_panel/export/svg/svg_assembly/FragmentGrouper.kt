package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly

import android.graphics.Color
import android.graphics.Point

/**
 * Job: Fragment Grouper.
 * Responsibility: Grouping closed path fragments by their exact ARGB color for downstream processing.
 */
object FragmentGrouper {

    data class GroupedFragments(
        val fragments: List<List<Point>>
    )

    fun group(
        closedPaths: List<List<Point>>,
        pathColors: List<Int>
    ): Map<Int, GroupedFragments> {
        // Key is the actual ARGB color integer
        val groups = mutableMapOf<Int, MutableList<List<Point>>>()

        pathColors.forEachIndexed { index, color ->
            // Skip fully transparent fragments (Alpha = 0)
            if (Color.alpha(color) > 0) {
                val path = closedPaths[index]
                groups.getOrPut(color) { mutableListOf() }.add(path)
            }
        }

        return groups.mapValues { (_, pathList) ->
            GroupedFragments(pathList)
        }
    }
}