package com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift.census_takers

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.ColorGroupSorter
import com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift.SeedFinder
import com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift.ThirdShiftCensusTaker
import com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift.CensusReport
import kotlinx.coroutines.ensureActive
import java.nio.ByteBuffer
import java.util.LinkedList
import java.util.Queue
import kotlin.coroutines.coroutineContext

/**
 * Census Taker #29 for the Third Shift.
 * Gold Standard Version: Sentinel-Aware & Family-Match Robust.
 * Statistical Upgrade: Returns a CensusReport for two-desk routing.
 */
object CensusTaker29 : ThirdShiftCensusTaker {

    override val id = 29
    private const val CANCELLATION_CHECK_INTERVAL = 1000

    override suspend fun analyzePath(
        path: List<Point>,
        quantizedImage: Bitmap,
        vramSlot: ByteBuffer
    ): CensusReport {
        if (path.isEmpty()) return CensusReport(Color.TRANSPARENT, Color.TRANSPARENT, 0, 0f, 0)

        // 1. Filter out sentinels (-1,-1) once at the start.
        val validPoints = path.filter { it.x >= 0 && it.y >= 0 }
        if (validPoints.isEmpty()) return CensusReport(Color.TRANSPARENT, Color.TRANSPARENT, 0, 0f, 0)

        // 2. Identify the color "Family" of the path border.
        val borderColors = mutableListOf<Int>()
        val sampleSize = minOf(validPoints.size, 20)
        val step = maxOf(1, validPoints.size / sampleSize)
        for (i in 0 until validPoints.size step step) {
            val p = validPoints[i]
            borderColors.add(quantizedImage.getPixel(p.x, p.y))
        }
        
        val identityColor = borderColors.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: Color.TRANSPARENT
        val identityGroup = ColorGroupSorter.getGroupIndexForPixel(identityColor)

        // 3. Create a bounding box for the area we are analyzing.
        val boundingBox = getPathBoundingBox(validPoints)
        val boxWidth = boundingBox.width()
        val boxHeight = boundingBox.height()
        if (boxWidth == 0 || boxHeight == 0) return CensusReport(identityColor, identityColor, 0, 0f, 0)

        // 4. Find a "Seed Point" inside the shape to start the fill.
        val seedPoint = SeedFinder.findSeedPoint(validPoints, quantizedImage)
        
        // Ensure the seed point belongs to the same color family.
        if (seedPoint == null || ColorGroupSorter.getGroupIndexForPixel(quantizedImage.getPixel(seedPoint.x, seedPoint.y)) != identityGroup) {
            return CensusReport(identityColor, identityColor, 0, 0f, 0)
        }

        val relativeSeedPoint = Point(seedPoint.x - boundingBox.left, seedPoint.y - boundingBox.top)
        
        val localPixels = IntArray(boxWidth * boxHeight)
        quantizedImage.getPixels(localPixels, 0, boxWidth, boundingBox.left, boundingBox.top, boxWidth, boxHeight)

        // 5. Run the Flood Fill to collect ALL colors in the interior.
        val interiorColors = if (vramSlot.capacity() >= neededMemory(boxWidth, boxHeight)) {
            runFloodFillVRAM(relativeSeedPoint, localPixels, boxWidth, boxHeight, vramSlot)
        } else {
            runFloodFillWithHeap(relativeSeedPoint, localPixels, boxWidth, boxHeight)
        }

        if (interiorColors.isEmpty()) return CensusReport(identityColor, identityColor, 0, 0f, 0)

        // 6. Perform the Census.
        val colorCounts = interiorColors.groupingBy { it }.eachCount()
        val sortedEntries = colorCounts.entries.sortedByDescending { it.value }
        val dominantEntry = sortedEntries[0]
        val secondaryColor = if (sortedEntries.size > 1) sortedEntries[1].key else dominantEntry.key
        
        val dominantColor = dominantEntry.key
        val dominantCount = dominantEntry.value
        val totalPixels = interiorColors.size

        // 7. Calculate Complexity.
        val blendRatio = (totalPixels - dominantCount).toFloat() / totalPixels.toFloat()
        val complexityScore = (blendRatio * 255).toInt().coerceIn(0, 255)

        return CensusReport(
            dominantColor = dominantColor,
            secondaryColor = secondaryColor,
            totalPixels = totalPixels,
            blendRatio = blendRatio,
            complexityScore = complexityScore
        )
    }

    private fun neededMemory(width: Int, height: Int): Int = (width * height) / 8

    private fun getPathBoundingBox(validPoints: List<Point>): Rect {
        var minX = validPoints.first().x
        var minY = validPoints.first().y
        var maxX = validPoints.first().x
        var maxY = validPoints.first().y

        for (i in 1 until validPoints.size) {
            val point = validPoints[i]
            if (point.x < minX) minX = point.x
            if (point.x > maxX) maxX = point.x
            if (point.y < minY) minY = point.y
            if (point.y > maxY) maxY = point.y
        }
        return Rect(minX, minY, maxX + 1, maxY + 1)
    }

    private suspend fun runFloodFillVRAM(
        startNode: Point,
        localPixels: IntArray,
        width: Int,
        height: Int,
        visitedVram: ByteBuffer
    ): List<Int> {
        val interiorColors = mutableListOf<Int>()
        val startColor = localPixels[startNode.y * width + startNode.x]
        val targetGroup = ColorGroupSorter.getGroupIndexForPixel(startColor)
        val queue: Queue<Point> = LinkedList()
        
        visitedVram.clear()
        queue.add(startNode)
        
        val startPixelIndex = startNode.y * width + startNode.x
        val startByteIndex = startPixelIndex / 8
        val startBitInByte = startPixelIndex % 8
        visitedVram.put(startByteIndex, (visitedVram.get(startByteIndex).toInt() or (1 shl startBitInByte)).toByte())

        var iteration = 0
        while (queue.isNotEmpty()) {
            if (iteration++ % CANCELLATION_CHECK_INTERVAL == 0) { coroutineContext.ensureActive() }
            val current = queue.poll()!!
            
            val pixelIdx = current.y * width + current.x
            interiorColors.add(localPixels[pixelIdx])
            
            val neighbors = listOf(
                Point(current.x, current.y - 1), 
                Point(current.x + 1, current.y), 
                Point(current.x, current.y + 1), 
                Point(current.x - 1, current.y)
            )
            
            for (neighbor in neighbors) {
                if (neighbor.x in 0 until width && neighbor.y in 0 until height) {
                    val pixelIndex = neighbor.y * width + neighbor.x
                    val byteIndex = pixelIndex / 8
                    val bitInByte = pixelIndex % 8
                    val isVisited = (visitedVram.get(byteIndex).toInt() and (1 shl bitInByte)) != 0
                    
                    if (!isVisited && ColorGroupSorter.getGroupIndexForPixel(localPixels[pixelIndex]) == targetGroup) {
                        visitedVram.put(byteIndex, (visitedVram.get(byteIndex).toInt() or (1 shl bitInByte)).toByte())
                        queue.add(neighbor)
                    }
                }
            }
        }
        return interiorColors
    }

    private suspend fun runFloodFillWithHeap(
        startNode: Point,
        localPixels: IntArray,
        width: Int,
        height: Int
    ): List<Int> {
        val interiorColors = mutableListOf<Int>()
        val startColor = localPixels[startNode.y * width + startNode.x]
        val targetGroup = ColorGroupSorter.getGroupIndexForPixel(startColor)
        val visited = BooleanArray(localPixels.size)
        val queue: Queue<Point> = LinkedList()
        
        queue.add(startNode)
        visited[startNode.y * width + startNode.x] = true
        
        var iteration = 0
        while (queue.isNotEmpty()) {
            if (iteration++ % CANCELLATION_CHECK_INTERVAL == 0) { coroutineContext.ensureActive() }
            val current = queue.poll()!!
            
            val pixelIdx = current.y * width + current.x
            interiorColors.add(localPixels[pixelIdx])
            
            val neighbors = listOf(
                Point(current.x, current.y - 1), 
                Point(current.x + 1, current.y), 
                Point(current.x, current.y + 1), 
                Point(current.x - 1, current.y)
            )

            for (neighbor in neighbors) {
                if (neighbor.x in 0 until width && neighbor.y in 0 until height) {
                    val pixelIndex = neighbor.y * width + neighbor.x
                    if (!visited[pixelIndex] && ColorGroupSorter.getGroupIndexForPixel(localPixels[pixelIndex]) == targetGroup) {
                        visited[pixelIndex] = true
                        queue.add(neighbor)
                    }
                }
            }
        }
        return interiorColors
    }
}
