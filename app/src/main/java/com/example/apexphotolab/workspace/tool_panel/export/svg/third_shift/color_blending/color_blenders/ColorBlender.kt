package com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.color_blending.color_blenders

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.CensusReport

/**
 * The blueprint for a Color Blender worker.
 * Every worker in the 30-file army must follow this protocol.
 * They are responsible for 13-point ladder sampling to identify multi-stop gradients.
 */
interface ColorBlender {
    val id: Int

    /**
     * Samples the source image along the path's axis and generates an SVG gradient string.
     * Returns a Pair containing:
     * 1. The <linearGradient> definition (The Recipe)
     * 2. The <path> element that applies it (The Result)
     */
    fun blend(
        path: List<Point>,
        report: CensusReport,
        sourceImage: Bitmap
    ): String
}
