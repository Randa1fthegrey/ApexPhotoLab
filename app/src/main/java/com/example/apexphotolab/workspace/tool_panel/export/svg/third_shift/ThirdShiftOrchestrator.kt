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
        quantizedImage: Bitmap,
        sourceImage: Bitmap
    ): Pair<List<List<Point>>, List<CensusReport>> {
        
        // 1. RESOLUTION: Determine the statistical CensusReport for every single fragment.
        val reports = ColoringDispatcher.resolveColorsInParallel(pathFragments, quantizedImage)
        
        // 2. GROUPING: Separate fragments by their color families for specialized consolidation.
        val familyGroups = pathFragments.indices.groupBy { index ->
            ColorGroupSorter.getGroupIndexForPixel(reports[index].dominantColor)
        }

        val allConsolidatedPaths = mutableListOf<List<Point>>()
        val allConsolidatedReports = mutableListOf<CensusReport>()

        // 3. SPECIALIZED CONSOLIDATION: Route each family to its dedicated team.
        familyGroups.forEach { (groupIndex, indices) ->
            val familyPaths = indices.map { pathFragments[it] }
            val familyReports = indices.map { reports[it] }

            val (resultPaths, resultReports) = when (groupIndex) {
                0 -> RedConsolidator.consolidate(familyPaths, familyReports)
                1 -> GreenConsolidator.consolidate(familyPaths, familyReports)
                2 -> BlueConsolidator.consolidate(familyPaths, familyReports)
                3 -> YellowConsolidator.consolidate(familyPaths, familyReports)
                4 -> CyanConsolidator.consolidate(familyPaths, familyReports)
                5 -> MagentaConsolidator.consolidate(familyPaths, familyReports)
                6 -> WhiteConsolidator.consolidate(familyPaths, familyReports)
                7 -> AlphaConsolidator.consolidate(familyPaths, familyReports)
                8 -> BlackConsolidator.consolidate(familyPaths, familyReports)
                9 -> GreyConsolidator.consolidate(familyPaths, familyReports)
                else -> Pair(familyPaths, familyReports)
            }

            allConsolidatedPaths.addAll(resultPaths)
            allConsolidatedReports.addAll(resultReports)
        }

        return Pair(allConsolidatedPaths, allConsolidatedReports)
    }
}
