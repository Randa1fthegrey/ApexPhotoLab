package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure

import android.graphics.Bitmap

/**
 * Job: Second Shift Pixel Extractor.
 * Responsibility: Extracting raw pixel arrays from a Bitmap for structural analysis.
 */
object SecondShiftPixelExtractor {

    fun extract(image: Bitmap): IntArray {
        val width = image.width
        val height = image.height
        val pixels = IntArray(width * height)
        image.getPixels(pixels, 0, width, 0, 0, width, height)
        return pixels
    }
}