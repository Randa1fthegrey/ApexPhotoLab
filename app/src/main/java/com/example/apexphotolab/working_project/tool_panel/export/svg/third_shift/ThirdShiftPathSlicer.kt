package com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift

import android.graphics.Point
import com.example.apexphotolab.working_project.tool_panel.export.svg.utils.CoreChecker
import kotlin.math.min

/**
 * The "Slicer" for the Third Shift (Path Coloring).
 * Divides the list of paths into a number of equal slices for parallel processing.
 */
object ThirdShiftPathSlicer {

    fun createSlices(paths: List<List<Point>>): List<List<Pair<Int, List<Point>>>> {
        val coreCount = CoreChecker.coreCount
        if (coreCount == 0 || paths.isEmpty()) {
            return emptyList()
        }

        val numSlices = min(paths.size, coreCount)
        if (numSlices == 0) {
            return emptyList()
        }

        // Distribute paths into a number of slices equal to numSlices (and coreCount, if enough paths)
        val slices = MutableList<MutableList<Pair<Int, List<Point>>>>(numSlices) { mutableListOf() }
        paths.forEachIndexed { index, path ->
            slices[index % numSlices].add(Pair(index, path))
        }
        return slices
    }
}
