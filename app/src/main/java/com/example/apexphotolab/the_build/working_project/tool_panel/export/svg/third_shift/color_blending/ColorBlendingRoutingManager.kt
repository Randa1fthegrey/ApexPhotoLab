package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.color_blending

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.ColorBucketDiagnostic
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.ColorGroupSorter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.CensusReport
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.color_blending.color_group_blenders.*

/**
 * Job: Routing manager for the color blending desk.
 * Responsibility: Receiving path shapes and census reports, then routing them to family-specific blending workers.
 */
object ColorBlendingRoutingManager {

    suspend fun route(
        pathFragments: List<List<Point>>,
        censusReports: List<CensusReport>,
        quantizedImage: Bitmap,
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

            ColorBucketDiagnostic.logShift3Routing(groupIndex, familyPaths.size)

            val result = when (groupIndex) {
                0 -> RedBlending.process(familyPaths, familyReports, quantizedImage, sourceImage)
                1 -> GreenBlending.process(familyPaths, familyReports, quantizedImage, sourceImage)
                2 -> BlueBlending.process(familyPaths, familyReports, quantizedImage, sourceImage)
                3 -> YellowBlending.process(familyPaths, familyReports, quantizedImage, sourceImage)
                4 -> CyanBlending.process(familyPaths, familyReports, quantizedImage, sourceImage)
                5 -> MagentaBlending.process(familyPaths, familyReports, quantizedImage, sourceImage)
                6 -> WhiteBlending.process(familyPaths, familyReports, quantizedImage, sourceImage)
                7 -> AlphaBlending.process(familyPaths, familyReports, quantizedImage, sourceImage)
                8 -> BlackBlending.process(familyPaths, familyReports, quantizedImage, sourceImage)
                9 -> GreyBlending.process(familyPaths, familyReports, quantizedImage, sourceImage)
                else -> emptyList()
            }
            
            if (result.isNotEmpty()) {
                allSvgPaths.add("<!-- === ${getBucketLabel(groupIndex)} === -->")
                allSvgPaths.addAll(result)
            }
        }

        return allSvgPaths
    }

    private fun getBucketLabel(index: Int): String {
        return when (index) {
            0 -> "RED BLOBS"
            1 -> "GREEN BLOBS"
            2 -> "BLUE BLOBS"
            3 -> "YELLOW BLOBS"
            4 -> "CYAN BLOBS"
            5 -> "MAGENTA BLOBS"
            6 -> "WHITE BLOBS"
            7 -> "ALPHA BLOBS"
            8 -> "BLACK BLOBS"
            9 -> "GREY BLOBS"
            else -> "UNKNOWN BLOBS"
        }
    }
}
