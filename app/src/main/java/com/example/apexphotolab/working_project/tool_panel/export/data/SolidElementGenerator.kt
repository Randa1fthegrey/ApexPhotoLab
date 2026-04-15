package com.example.apexphotolab.working_project.tool_panel.export.data

import android.graphics.Point
import com.example.apexphotolab.working_project.tool_panel.export.svg.svg_assembly.artist.SolidFillGenerator

/**
 * A single-responsibility processor for generating solid-colored SVG elements.
 */
object SolidElementGenerator {

    private const val MIN_PATH_SIZE = 2 // The "dust filter"

    /**
     * Takes the results from the path tracing and coloring shifts, filters them,
     * and generates the final SVG <path> elements for all solid shapes.
     *
     * @param closedPaths The list of all closed paths found.
     * @param pathColors The list of corresponding colors for each path.
     * @return A list of strings, each being a complete SVG <path> element.
     */
    fun generate(closedPaths: List<List<Point>>, pathColors: List<Int>): List<String> {
        val solidElements = mutableListOf<String>()
        closedPaths.forEachIndexed { index, path ->
            if (path.size > MIN_PATH_SIZE) {
                val argbColor = pathColors[index]
                // The check for opacity has been removed. All solid paths are now processed.
                solidElements.add(SolidFillGenerator.generate(listOf(path), argbColor))
            }
        }
        return solidElements
    }
}