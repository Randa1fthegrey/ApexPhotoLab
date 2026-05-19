package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.SolidFillGenerator

/**
 * Job: Solid Element Generator.
 * Responsibility: Filtering traced paths and generating the final SVG path elements for all solid shapes.
 */
object SolidElementGenerator {

    fun generate(closedPaths: List<List<Point>>, pathColors: List<Int>): List<String> {
        val solidElements = mutableListOf<String>()
        closedPaths.forEachIndexed { index, path ->
            if (path.size > MIN_PATH_SIZE) {
                val argbColor = pathColors[index]
                solidElements.add(SolidFillGenerator.generate(listOf(path), argbColor))
            }
        }
        return solidElements
    }

    private const val MIN_PATH_SIZE = 2
}
