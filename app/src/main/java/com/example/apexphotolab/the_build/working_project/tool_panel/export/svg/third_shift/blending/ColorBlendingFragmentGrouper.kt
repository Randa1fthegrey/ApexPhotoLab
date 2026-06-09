package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.blending

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure.ColorGroupSorter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusReport

/**
 * Job: Color Blending Fragment Grouper.
 * Responsibility: Categorizing path fragments into color-family groups based on their statistical dominant color.
 */
object ColorBlendingFragmentGrouper {

    fun group(
        pathFragments: List<List<Point>>,
        censusReports: List<CensusReport>
    ): Map<Int, List<Int>> {
        return pathFragments.indices.groupBy { index ->
            ColorGroupSorter.getGroupIndexForPixel(censusReports[index].dominantColor)
        }
    }
}