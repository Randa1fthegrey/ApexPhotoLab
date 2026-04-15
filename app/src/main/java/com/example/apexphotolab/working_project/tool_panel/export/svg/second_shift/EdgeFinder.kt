package com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift

import android.graphics.Point

/**
 * A utility tool that finds the perimeter pixels of a given blob.
 * Updated: Uses 8-way neighbor check and correctly handles canvas boundaries.
 */
object EdgeFinder {
    
    /**
     * Finds pixels that border a different color OR the canvas boundary.
     * Checks all 8 surrounding neighbors to ensure sharp tips are captured.
     */
    fun findEdges(blob: HashSet<Point>, width: Int, height: Int): HashSet<Point> {
        val edgePixels = HashSet<Point>()
        
        val offsets = arrayOf(
            Point(0, -1), Point(1, -1), Point(1, 0), Point(1, 1),
            Point(0, 1), Point(-1, 1), Point(-1, 0), Point(-1, -1)
        )

        for (point in blob) {
            var isEdge = false
            
            for (offset in offsets) {
                val nx = point.x + offset.x
                val ny = point.y + offset.y
                
                // 1. Boundary Check: Outside the canvas is always an edge!
                if (nx < 0 || nx >= width || ny < 0 || ny >= height) {
                    isEdge = true
                    break
                }
                
                // 2. Color Check: Neighboring a different color is an edge.
                if (!blob.contains(Point(nx, ny))) {
                    isEdge = true
                    break
                }
            }

            if (isEdge) {
                edgePixels.add(point)
            }
        }
        return edgePixels
    }
}
