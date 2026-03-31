package com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.ColorGroupSorter
import com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.consolidators.*

/**
 * The Master Orchestrator for the Third Shift (Path Coloring & Resolution).
 * Standardized Version: Processes all fragments equally to ensure 
 * consistent shape discovery and consolidation.
 */
object ThirdShiftOrchestrator {

    /**
     * Resolves colors and consolidates fragments into logical elements.
     */
    suspend fun run(
        pathFragments: List<List<Point>>, 
        quantizedImage: Bitmap
    ): Pair<List<List<Point>>, List<Int>> {
        
        // 1. RESOLUTION: Determine the raw colors for every single fragment.
        val rawColors = ColoringDispatcher.resolveColorsInParallel(pathFragments, quantizedImage)
        
        // 2. GROUPING: Separate fragments by their color families for specialized consolidation.
        val familyGroups = pathFragments.indices.groupBy { index ->
            ColorGroupSorter.getGroupIndexForPixel(rawColors[index])
        }

        val allConsolidatedPaths = mutableListOf<List<Point>>()
        val allConsolidatedColors = mutableListOf<Int>()

        // 3. SPECIALIZED CONSOLIDATION: Route each family to its dedicated team.
        familyGroups.forEach { (groupIndex, indices) ->
            val familyPaths = indices.map { pathFragments[it] }
            val familyColors = indices.map { rawColors[it] }

            val (resultPaths, resultColors) = when (groupIndex) {
                0 -> RedConsolidator.consolidate(familyPaths, familyColors)
                1 -> GreenConsolidator.consolidate(familyPaths, familyColors)
                2 -> BlueConsolidator.consolidate(familyPaths, familyColors)
                3 -> YellowConsolidator.consolidate(familyPaths, familyColors)
                4 -> CyanConsolidator.consolidate(familyPaths, familyColors)
                5 -> MagentaConsolidator.consolidate(familyPaths, familyColors)
                6 -> WhiteConsolidator.consolidate(familyPaths, familyColors)
                7 -> AlphaConsolidator.consolidate(familyPaths, familyColors)
                8 -> BlackConsolidator.consolidate(familyPaths, familyColors)
                9 -> GreyConsolidator.consolidate(familyPaths, familyColors)
                else -> Pair(familyPaths, familyColors)
            }

            allConsolidatedPaths.addAll(resultPaths)
            allConsolidatedColors.addAll(resultColors)
        }

        return Pair(allConsolidatedPaths, allConsolidatedColors)
    }
}
