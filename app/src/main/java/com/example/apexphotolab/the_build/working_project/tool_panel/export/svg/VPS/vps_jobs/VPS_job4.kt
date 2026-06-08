package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.vps_jobs

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.PathDataGenerator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.CensusReport

/**
 * Job: VPS Job 4 - Blending.
 * Responsibility: Performing axis-sampling to produce an SVG gradient string for a path.
 */
object VPS_job4 {

    data class BlendingData(
        val path: List<Point>,
        val report: CensusReport,
        val quantizedImage: Bitmap,
        val sourceImage: Bitmap,
        var svgResult: String = ""
    )

    suspend fun execute(workerId: Int, data: Any?) {
        val bData = data as? BlendingData ?: return
        val path = bData.path
        val sourceImage = bData.sourceImage

        if (path.isEmpty()) {
            bData.svgResult = ""
            return
        }

        val bounds = getPathBoundingBox(path)
        val width = bounds.width()
        val height = bounds.height()
        if (width == 0 || height == 0) {
            bData.svgResult = ""
            return
        }

        val isHorizontal = width > height
        val stops = mutableListOf<String>()
        val sampleCount = 13

        for (i in 0 until sampleCount) {
            val progress = i.toFloat() / (sampleCount - 1)
            val offsetX = if (isHorizontal) (width * progress).toInt() else width / 2
            val offsetY = if (!isHorizontal) (height * progress).toInt() else height / 2

            val sampleX = (bounds.left + offsetX).coerceIn(0, sourceImage.width - 1)
            val sampleY = (bounds.top + offsetY).coerceIn(0, sourceImage.height - 1)

            val pixel = sourceImage.getPixel(sampleX, sampleY)
            val hexColor = String.format("#%06X", 0xFFFFFF and pixel)
            val offsetPercent = (progress * 100).toInt()

            stops.add("<stop offset=\"$offsetPercent%\" stop-color=\"$hexColor\" />")
        }

        val gradId = "grad_${System.identityHashCode(path)}"
        val x1 = "0%"
        val y1 = "0%"
        val x2 = if (isHorizontal) "100%" else "0%"
        val y2 = if (isHorizontal) "0%" else "100%"

        val pathData = PathDataGenerator.generate(path)

        bData.svgResult = buildString {
            append("<linearGradient id=\"$gradId\" x1=\"$x1\" y1=\"$y1\" x2=\"$x2\" y2=\"$y2\">\n")
            stops.forEach { append("  $it\n") }
            append("</linearGradient>\n")
            append("<path d=\"$pathData\" fill=\"url(#$gradId)\" />")
        }
    }

    private fun getPathBoundingBox(path: List<Point>): Rect {
        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var maxY = Int.MIN_VALUE
        for (p in path) {
            if (p.x == -1) continue
            if (p.x < minX) minX = p.x
            if (p.x > maxX) maxX = p.x
            if (p.y < minY) minY = p.y
            if (p.y > maxY) maxY = p.y
        }
        return Rect(minX, minY, maxX, maxY)
    }
}