package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color

import android.graphics.Bitmap

/**
 * Job: Pixel Extractor.
 * Responsibility: Extracting a raw IntArray of pixels from an Android Bitmap.
 */
object QuantizationPixelExtractor {

    fun extract(image: Bitmap): IntArray {
        val width = image.width
        val height = image.height
        val pixels = IntArray(width * height)
        image.getPixels(pixels, 0, width, 0, 0, width, height)
        return pixels
    }
}
