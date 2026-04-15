package com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift.color_blending

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.ColorGroupSorter
import com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift.CensusReport
import com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift.color_blending.color_mgrs.*

/**
 * The routing manager for the color blending desk.
 * Receives shapes from the AssemblyOrchestrator and routes them to family-specific managers.
 */
object ColorBlendingRoutingManager {

    suspend fun route(
        pathFragments: List<List<Point>>,
        censusReports: List<CensusReport>,
        sourceImage: Bitmap
    ): List<String> {

        // Group indices by their color family using the statistical dominant color
        val familyGroups = pathFragments.indices.groupBy { index ->
            ColorGroupSorter.getGroupIndexForPixel(censusReports[index].dominantColor)
        }

        val allSvgPaths = mutableListOf<String>()

        familyGroups.forEach { (groupIndex, indices) ->
            val familyPaths = indices.map { pathFragments[it] }
            val familyReports = indices.map { censusReports[it] }

            val result = when (groupIndex) {
                0 -> RedBlendingManager.process(familyPaths, familyReports, sourceImage)
                1 -> GreenBlendingManager.process(familyPaths, familyReports, sourceImage)
                2 -> BlueBlendingManager.process(familyPaths, familyReports, sourceImage)
                3 -> YellowBlendingManager.process(familyPaths, familyReports, sourceImage)
                4 -> CyanBlendingManager.process(familyPaths, familyReports, sourceImage)
                5 -> MagentaBlendingManager.process(familyPaths, familyReports, sourceImage)
                6 -> WhiteBlendingManager.process(familyPaths, familyReports, sourceImage)
                7 -> AlphaBlendingManager.process(familyPaths, familyReports, sourceImage)
                8 -> BlackBlendingManager.process(familyPaths, familyReports, sourceImage)
                9 -> GreyBlendingManager.process(familyPaths, familyReports, sourceImage)
                else -> emptyList()
            }
            allSvgPaths.addAll(result)
        }

        return allSvgPaths
    }
}