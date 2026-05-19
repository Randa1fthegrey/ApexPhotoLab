package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.third_shift_essential

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.ColorGroupSorter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.CensusReport
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.ColoringDispatcher
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.consolidators.AlphaConsolidator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.consolidators.BlackConsolidator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.consolidators.BlueConsolidator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.consolidators.CyanConsolidator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.consolidators.GreenConsolidator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.consolidators.GreyConsolidator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.consolidators.MagentaConsolidator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.consolidators.RedConsolidator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.consolidators.WhiteConsolidator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.consolidators.YellowConsolidator

/**
 * Job: Third Shift Orchestrator.
 * Responsibility: Running the full third shift pipeline — resolving colors and
 * consolidating path fragments into logical elements.
 */
object ThirdShiftOrchestrator {

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