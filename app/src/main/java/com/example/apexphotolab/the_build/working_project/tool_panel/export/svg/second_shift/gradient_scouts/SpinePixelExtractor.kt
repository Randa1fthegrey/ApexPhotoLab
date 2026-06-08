package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Bitmap

/**
 * Job: Spine Pixel Extractor.
 * Responsibility: Extracting raw pixel arrays from both quantized and original Bitmaps for analysis.
 */
object SpinePixelExtractor {

    fun extract(image: Bitmap): IntArray {
        val width = image.width
        val height = image.height
        val pixels = IntArray(width * height)
        image.getPixels(pixels, 0, width, 0, 0, width, height)
        return pixels
    }
}
