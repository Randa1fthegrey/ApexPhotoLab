package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.gradient_scouts

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.ColorGroupSorter

/**
 * Job: The Surveyor (Orchestrator).
 * Responsibility: Coordinating the pixel extraction, scoring, and ridge walking specialists to discover intensity spines.
 */
object SpineLogic {

    /**
     * Finds the peak intensity path (the spine) within a specific color shape.
     */
    fun findSpine(quantizedImage: Bitmap, originalImage: Bitmap, groupIndex: Int): List<List<Point>> {
        val width = quantizedImage.width
        val height = quantizedImage.height

        // 1. EXTRACTION
        val qPixels = SpinePixelExtractor.extract(quantizedImage)
        val oPixels = SpinePixelExtractor.extract(originalImage)

        // 2. SCORING
        val scores = FloatArray(width * height) { -1f }
        val targetPoints = mutableListOf<Int>()

        for (i in qPixels.indices) {
            val qPixel = qPixels[i]
            // Only look within the SHAPE defined by the quantizer
            if (ColorGroupSorter.getGroupIndexForPixel(qPixel) == groupIndex) {
                scores[i] = SpineIntensityScorer.calculateScore(oPixels[i], groupIndex)
                targetPoints.add(i)
            }
        }

        if (targetPoints.isEmpty()) return emptyList()

        // 3. RIDGE WALKING
        return SpineRidgeWalker.walk(width, height, targetPoints, scores)
    }
}
