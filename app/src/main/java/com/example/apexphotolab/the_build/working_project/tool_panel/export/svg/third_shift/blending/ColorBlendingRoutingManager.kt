package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.blending

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.fourth_shift.rendering.SolidFillGenerator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census.CensusReport

/**
 * Job: Routing manager for the color blending desk (Orchestrator).
 * Responsibility: Coordinating fragment grouping and generating final SVG elements using the VPS.
 */
object ColorBlendingRoutingManager {

    suspend fun route(
        pathFragments: List<List<Point>>,
        censusReports: List<CensusReport>,
        quantizedImage: Bitmap,
        sourceImage: Bitmap
    ): List<String> {

        // 1. GROUPING
        val familyGroups = ColorBlendingFragmentGrouper.group(pathFragments, censusReports)

        val allSvgPaths = mutableListOf<String>()

        // 2. RENDERING (Direct VPS/Artist Integration)
        
        for ((groupIndex, indices) in familyGroups.entries.sortedBy { it.key }) {
            if (indices.isEmpty()) continue
            if (groupIndex == 7) continue // Alpha / Translucent background - leave canvas 100% transparent!

            val label = ColorBlendingBucketLabeller.getLabel(groupIndex)
            allSvgPaths.add("<!-- === $label === -->")

            for (index in indices) {
                val path = pathFragments[index]
                val color = censusReports[index].dominantColor
                val svgSnippet = SolidFillGenerator.generate(listOf(path), color)
                if (svgSnippet.isNotEmpty()) {
                    allSvgPaths.add(svgSnippet)
                }
            }
        }

        return allSvgPaths
    }
}
