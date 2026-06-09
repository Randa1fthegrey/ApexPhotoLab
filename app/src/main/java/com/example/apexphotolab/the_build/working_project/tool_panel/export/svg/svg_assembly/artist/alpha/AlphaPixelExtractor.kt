package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.alpha

import android.graphics.Bitmap

/**
 * Job: Alpha Pixel Extractor.
 * Responsibility: Extracting raw pixel data from a Bitmap for transparency detection.
 */
object AlphaPixelExtractor {

    fun extract(image: Bitmap): IntArray {
        val width = image.width
        val height = image.height
        val pixels = IntArray(width * height)
        image.getPixels(pixels, 0, width, 0, 0, width, height)
        return pixels
    }
}