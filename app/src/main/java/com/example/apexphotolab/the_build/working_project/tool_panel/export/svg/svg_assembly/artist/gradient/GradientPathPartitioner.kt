package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.gradient

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.PathDataGenerator

/**
 * Job: Gradient Path Partitioner.
 * Responsibility: Splitting a list of paths into closed loop data and open fragment data.
 */
object GradientPathPartitioner {

    data class PartitionedPaths(val closedData: String, val openData: String)

    fun partition(paths: List<List<Point>>): PartitionedPaths {
        val closedPathData = StringBuilder()
        val openPathData = StringBuilder()

        paths.forEach { path ->
            val (data, isClosed) = PathDataGenerator.generateWithStatus(path)
            if (isClosed) {
                closedPathData.append(data).append(" ")
            } else {
                openPathData.append(data).append(" ")
            }
        }

        return PartitionedPaths(closedPathData.toString().trim(), openPathData.toString().trim())
    }
}