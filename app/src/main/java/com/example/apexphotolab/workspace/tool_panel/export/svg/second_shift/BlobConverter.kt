package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift

import android.graphics.Point

/**
 * Job #2: The Conversion Department.
 * Converts raw pixel indices into a geometric HashSet of Points.
 */
object BlobConverter {

    /**
     * Translates a list of flat pixel indices into a coordinate-based blob.
     */
    fun convert(indices: List<Int>, width: Int): HashSet<Point> {
        val blob = HashSet<Point>(indices.size)
        indices.forEach { idx ->
            blob.add(Point(idx % width, idx / width))
        }
        return blob
    }
}
