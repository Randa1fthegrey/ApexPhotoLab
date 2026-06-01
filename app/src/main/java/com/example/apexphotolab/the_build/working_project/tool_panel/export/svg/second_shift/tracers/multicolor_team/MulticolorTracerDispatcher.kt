package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.multicolor_team

import android.graphics.Point

/**
 * Job: Multicolor Tracer Dispatcher.
 * Responsibility: Orchestrating multi-color blob detection and boundary tracing, producing unified shape outlines and identifying claimed pixels for pipeline exclusion.
 */
object MulticolorTracerDispatcher {

    /**
     * Scans the image for multi-color blobs.
     * Returns a Pair:
     * 1. The list of traced boundary paths.
     * 2. The set of all pixel indices claimed by these blobs.
     */
    fun scan(pixels: IntArray, width: Int, height: Int): Pair<List<List<Point>>, Set<Int>> {
        val blobs = MulticolorBlobDetector.detect(pixels, width, height)
        if (blobs.isEmpty()) return Pair(emptyList(), emptySet())

        val allPaths = mutableListOf<List<Point>>()
        val claimedIndices = mutableSetOf<Int>()

        for (blob in blobs) {
            val paths = MulticolorBoundaryTracer.trace(blob)
            allPaths.addAll(paths)
            claimedIndices.addAll(blob.pixelIndices)
        }

        return Pair(allPaths, claimedIndices)
    }
}
