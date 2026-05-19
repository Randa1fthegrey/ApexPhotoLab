package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect

/**
 * Job: Seed Finder.
 * Responsibility: Finding a guaranteed interior pixel of a path shape
 * using the scanline middle strategy.
 */
object SeedFinder {

    fun findSeedPoint(path: List<Point>, image: Bitmap): Point? {
        val validPath = path.filter { it.x >= 0 && it.y >= 0 }
        if (validPath.size < 3) return null

        val boundingBox = getPathBoundingBox(validPath)
        val midY = boundingBox.centerY()

        // 1. Find all points on the path that are on the middle scanline.
        val pointsOnMidLine = validPath.filter { it.y == midY }.sortedBy { it.x }

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

        // 3. Fallback: move 1 pixel toward the center of the bounding box.
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
