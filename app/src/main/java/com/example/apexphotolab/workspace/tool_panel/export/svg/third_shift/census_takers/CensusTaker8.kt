package com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.census_takers

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.ColorGroupSorter
import com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.SeedFinder
import com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.ThirdShiftCensusTaker
import com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.CensusReport
import kotlinx.coroutines.ensureActive
import java.nio.ByteBuffer
import java.util.LinkedList
import java.util.Queue
import kotlin.coroutines.coroutineContext

/**
 * Census Taker #8 for the Third Shift.
 * Statistical Upgrade: Returns a CensusReport for two-desk routing.
 */
object CensusTaker8 : ThirdShiftCensusTaker {

    override val id = 8
    private const val CANCELLATION_CHECK_INTERVAL = 1000

    override suspend fun analyzePath(
        path: List<Point>,
        quantizedImage: Bitmap,
        vramSlot: ByteBuffer
    ): CensusReport {
        if (path.isEmpty()) return CensusReport(Color.TRANSPARENT, Color.TRANSPARENT, 0, 0f, 0)
        val validPoints = path.filter { it.x >= 0 && it.y >= 0 }
        if (validPoints.isEmpty()) return CensusReport(Color.TRANSPARENT, Color.TRANSPARENT, 0, 0f, 0)

        val borderColors = mutableListOf<Int>()
        val sampleSize = minOf(validPoints.size, 20)
        val step = maxOf(1, validPoints.size / sampleSize)
        for (i in 0 until validPoints.size step step) {
            borderColors.add(quantizedImage.getPixel(validPoints[i].x, validPoints[i].y))
        }
        
        val identityColor = borderColors.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: Color.TRANSPARENT
        val identityGroup = ColorGroupSorter.getGroupIndexForPixel(identityColor)

        val boundingBox = getPathBoundingBox(validPoints)
        val boxWidth = boundingBox.width()
        val boxHeight = boundingBox.height()
        if (boxWidth == 0 || boxHeight == 0) return CensusReport(identityColor, identityColor, 0, 0f, 0)

        val seedPoint = SeedFinder.findSeedPoint(validPoints, quantizedImage)
        if (seedPoint == null || ColorGroupSorter.getGroupIndexForPixel(quantizedImage.getPixel(seedPoint.x, seedPoint.y)) != identityGroup) {
            return CensusReport(identityColor, identityColor, 0, 0f, 0)
        }

        val relativeSeedPoint = Point(seedPoint.x - boundingBox.left, seedPoint.y - boundingBox.top)
        val localPixels = IntArray(boxWidth * boxHeight)
        quantizedImage.getPixels(localPixels, 0, boxWidth, boundingBox.left, boundingBox.top, boxWidth, boxHeight)

        val interiorColors = if (vramSlot.capacity() >= neededMemory(boxWidth, boxHeight)) {
            runFloodFillVRAM(relativeSeedPoint, localPixels, boxWidth, boxHeight, vramSlot)
        } else {
            runFloodFillWithHeap(relativeSeedPoint, localPixels, boxWidth, boxHeight)
        }

        if (interiorColors.isEmpty()) return CensusReport(identityColor, identityColor, 0, 0f, 0)

        val colorCounts = interiorColors.groupingBy { it }.eachCount()
        val sortedEntries = colorCounts.entries.sortedByDescending { it.value }
        val dominantEntry = sortedEntries[0]
        val secondaryColor = if (sortedEntries.size > 1) sortedEntries[1].key else dominantEntry.key

        val dominantColor = dominantEntry.key
        val dominantCount = dominantEntry.value
        val totalPixels = interiorColors.size

        val blendRatio = (totalPixels - dominantCount).toFloat() / totalPixels.toFloat()
        val complexityScore = (blendRatio * 255).toInt().coerceIn(0, 255)

        return CensusReport(dominantColor, secondaryColor, totalPixels, blendRatio, complexityScore)
    }

    private fun neededMemory(width: Int, height: Int): Int = (width * height) / 8

    private fun getPathBoundingBox(validPoints: List<Point>): Rect {
        var minX = validPoints.first().x
        var minY = validPoints.first().y
        var maxX = validPoints.first().x
        var maxY = validPoints.first().y
        for (i in 1 until validPoints.size) {
            val p = validPoints[i]
            if (p.x < minX) minX = p.x else if (p.x > maxX) maxX = p.x
            if (p.y < minY) minY = p.y else if (p.y > maxY) maxY = p.y
        }
        return Rect(minX, minY, maxX + 1, maxY + 1)
    }

    private suspend fun runFloodFillVRAM(startNode: Point, localPixels: IntArray, width: Int, height: Int, visitedVram: ByteBuffer): List<Int> {
        val interiorColors = mutableListOf<Int>()
        val startColor = localPixels[startNode.y * width + startNode.x]
        val targetGroup = ColorGroupSorter.getGroupIndexForPixel(startColor)
        val queue: Queue<Point> = LinkedList()
        visitedVram.clear()
        queue.add(startNode)
        val startIdx = startNode.y * width + startNode.x
        visitedVram.put(startIdx / 8, (visitedVram.get(startIdx / 8).toInt() or (1 shl (startIdx % 8))).toByte())
        var iteration = 0
        while (queue.isNotEmpty()) {
            if (iteration++ % CANCELLATION_CHECK_INTERVAL == 0) coroutineContext.ensureActive()
            val current = queue.poll()!!
            interiorColors.add(localPixels[current.y * width + current.x])
            val neighbors = arrayOf(Point(current.x, current.y - 1), Point(current.x + 1, current.y), Point(current.x, current.y + 1), Point(current.x - 1, current.y))
            for (neighbor in neighbors) {
                if (neighbor.x in 0 until width && neighbor.y in 0 until height) {
                    val pixelIdx = neighbor.y * width + neighbor.x
                    if (((visitedVram.get(pixelIdx / 8).toInt() and (1 shl (pixelIdx % 8))) == 0) && ColorGroupSorter.getGroupIndexForPixel(localPixels[pixelIdx]) == targetGroup) {
                        visitedVram.put(pixelIdx / 8, (visitedVram.get(pixelIdx / 8).toInt() or (1 shl (pixelIdx % 8))).toByte())
                        queue.add(neighbor)
                    }
                }
            }
        }
        return interiorColors
    }

    private suspend fun runFloodFillWithHeap(startNode: Point, localPixels: IntArray, width: Int, height: Int): List<Int> {
        val interiorColors = mutableListOf<Int>()
        val startColor = localPixels[startNode.y * width + startNode.x]
        val targetGroup = ColorGroupSorter.getGroupIndexForPixel(startColor)
        val visited = BooleanArray(localPixels.size)
        val queue: Queue<Point> = LinkedList()
        queue.add(startNode)
        visited[startNode.y * width + startNode.x] = true
        var iteration = 0
        while (queue.isNotEmpty()) {
            if (iteration++ % CANCELLATION_CHECK_INTERVAL == 0) coroutineContext.ensureActive()
            val current = queue.poll()!!
            interiorColors.add(localPixels[current.y * width + current.x])
            val neighbors = arrayOf(Point(current.x, current.y - 1), Point(current.x + 1, current.y), Point(current.x, current.y + 1), Point(current.x - 1, current.y))
            for (neighbor in neighbors) {
                if (neighbor.x in 0 until width && neighbor.y in 0 until height) {
                    val pixelIdx = neighbor.y * width + neighbor.x
                    if (!visited[pixelIdx] && ColorGroupSorter.getGroupIndexForPixel(localPixels[pixelIdx]) == targetGroup) {
                        visited[pixelIdx] = true
                        queue.add(neighbor)
                    }
                }
            }
        }
        return interiorColors
    }
}
