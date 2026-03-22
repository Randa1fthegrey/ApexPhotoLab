package com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly

import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist.SolidFillGenerator
import com.example.apexphotolab.workspace.tool_panel.export.svg.utils.CoreHighwayFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * The main orchestrator for the new Assembly Shift.
 * Takes clean path fragments and groups them into final SVG shapes.
 */
object AssemblyOrchestrator {

    /**
     * Receives path fragments from the Third Shift and coordinates the final painting.
     * Final Fix: Sorts by pixel count DESCENDING to ensure backgrounds are at the BOTTOM.
     */
    suspend fun run(
        pathFragments: List<List<Point>>,
        pathColors: List<Int>,
        allEdges: HashSet<Point>
    ): List<String> = coroutineScope {

        // 1. Group the fragments by their color family.
        val fragmentsByFamily = FragmentGrouper.group(pathFragments, pathColors)

        // 2. Sort families by total pixel count (DESCENDING).
        // This is the most reliable way to ensure the largest shapes (backgrounds) are drawn first.
        val sortedFamilies = fragmentsByFamily.toList().sortedByDescending { it.second.originalColors.size }

        val highways = CoreHighwayFactory.coreHighways

        // 3. Process each color family in parallel.
        val jobs = sortedFamilies.mapIndexed { index, (groupIndex, groupData) ->
            val (fragments, originalColors) = groupData
            val highway = if (highways.isNotEmpty()) highways[index % highways.size] else coroutineContext

            async(highway) {
                if (fragments.isNotEmpty()) {
                    val finalColor = findDominantColor(originalColors)
                    val svgElement = SolidFillGenerator.generate(fragments, finalColor)
                    svgElement
                } else {
                    ""
                }
            }
        }

        return@coroutineScope jobs.awaitAll().filter { it.isNotEmpty() }
    }

    private fun findDominantColor(colors: List<Int>): Int {
        if (colors.isEmpty()) return 0
        val validColors = colors.filter { android.graphics.Color.alpha(it) > 0 }
        if (validColors.isEmpty()) return colors.first()

        return validColors.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: validColors.first()
    }
}
