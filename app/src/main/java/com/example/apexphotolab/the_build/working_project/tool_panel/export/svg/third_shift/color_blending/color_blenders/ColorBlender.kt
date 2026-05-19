package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.color_blending.color_blenders

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.CensusReport

/**
 * Job: Blueprint for a ColorBlender worker.
 * Responsibility: Defining the contract for 13-point ladder sampling that produces an SVG gradient string.
 */
interface ColorBlender {
    val id: Int

    fun blend(
        path: List<Point>,
        report: CensusReport,
        quantizedImage: Bitmap,
        sourceImage: Bitmap
    ): String
}
