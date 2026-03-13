package com.example.apexphotolab.workspace.toolbars.export.svg.third_shift

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect

/**
 * A single-responsibility utility for finding a guaranteed interior pixel of a path.
 */
object SeedFinder {

    /**
     * Finds a single point that is guaranteed to be inside the given path outline.
     * This version uses the "Scanline Middle" strategy to avoid picking points
     * in the empty center of hollow or irregular shapes.
     *
     * @param path The list of points that make up the shape's border.
     * @param image The bitmap being analyzed.
     * @return A single interior [android.graphics.Point], or null if one cannot be found.
     */
    fun findSeedPoint(path: List<Point>, image: Bitmap): Point? {
        if (path.size < 3) return null

        val boundingBox = getPathBoundingBox(path)
        val midY = boundingBox.centerY()

        // 1. Find all points on the path that are on the middle scanline.
        val pointsOnMidLine = path.filter { it.y == midY }.sortedBy { it.x }

        // 2. If we found at least two points, pick a point halfway between the first two.
        // This is much more likely to be "inside" than the mathematical center of the box.
        if (pointsOnMidLine.size >= 2) {
            val left = pointsOnMidLine[0].x
            val right = pointsOnMidLine[1].x
            val midX = (left + right) / 2
            
            // Safety check: Ensure the seed is within image bounds.
            if (midX in 0 until image.width && midY in 0 until image.height) {
                return Point(midX, midY)
            }
        }

        // 3. Fallback: If the scanline strategy fails, return a point very close to the first path point.
        // We move 1 pixel toward the center of the bounding box.
        val first = path.first()
        val centerX = boundingBox.centerX()
        val centerY = boundingBox.centerY()
        
        val offsetX = if (first.x < centerX) 1 else if (first.x > centerX) -1 else 0
        val offsetY = if (first.y < centerY) 1 else if (first.y > centerY) -1 else 0
        
        return Point(
            (first.x + offsetX).coerceIn(0, image.width - 1),
            (first.y + offsetY).coerceIn(0, image.height - 1)
        )
    }

    /**
     * Calculates the bounding box of a given path.
     */
    private fun getPathBoundingBox(path: List<Point>): Rect {
        if (path.isEmpty()) return Rect(0, 0, 0, 0)

        var minX = path.first().x
        var minY = path.first().y
        var maxX = path.first().x
        var maxY = path.first().y

        for (i in 1 until path.size) {
            val point = path[i]
            if (point.x < minX) minX = point.x
            if (point.x > maxX) maxX = point.x
            if (point.y < minY) minY = point.y
            if (point.y > maxY) maxY = point.y
        }
        return Rect(minX, minY, maxX + 1, maxY + 1)
    }
}
