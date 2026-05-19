package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift

import android.graphics.Point

/**
 * Job: Blob Converter.
 * Responsibility: Converting raw pixel indices into a geometric HashSet of Points.
 */
object BlobConverter {

    fun convert(indices: List<Int>, width: Int): HashSet<Point> {
        val blob = HashSet<Point>(indices.size)
        indices.forEach { idx ->
            blob.add(Point(idx % width, idx / width))
        }
        return blob
    }
}
