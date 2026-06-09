package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.dispatchers

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.CoreChecker
import kotlin.math.min

/**
 * Job: Third Shift Path Slicer.
 * Responsibility: Dividing the path list into equal slices for parallel processing
 * across available CPU cores.
 */
object ThirdShiftPathSlicer {

    fun createSlices(paths: List<List<Point>>): List<List<Pair<Int, List<Point>>>> {
        val coreCount = CoreChecker.coreCount
        if (coreCount == 0 || paths.isEmpty()) return emptyList()

        val numSlices = min(paths.size, coreCount)
        if (numSlices == 0) return emptyList()

        val slices = MutableList<MutableList<Pair<Int, List<Point>>>>(numSlices) { mutableListOf() }
        paths.forEachIndexed { index, path ->
            slices[index % numSlices].add(Pair(index, path))
        }
        return slices
    }
}