package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.tracers

import android.graphics.Point
import java.nio.ByteBuffer
import kotlin.math.abs

/**
 * The "Lonely Walker" Path Tracer specifically for GREYSCALE shapes.
 * Final VRAM Version: Uses bitmasks for zero-heap coordinate lookup.
 */
object GreyPathTracer {

    private val OFFSETS = arrayOf(
        Point(0, -1),  // 0: N
        Point(1, -1),  // 1: NE
        Point(1, 0),   // 2: E
        Point(1, 1),   // 3: SE
        Point(0, 1),   // 4: S
        Point(-1, 1),  // 5: SW
        Point(-1, 0),  // 6: W
        Point(-1, -1)  // 7: NW
    )

    fun trace(edgePixels: HashSet<Point>, vram: ByteBuffer, width: Int): List<List<Point>> {
        val allPaths = mutableListOf<List<Point>>()
        val remainingPixels = edgePixels.toMutableSet()
        
        val homeCandidates = edgePixels.sortedBy { it.y * 10000 + it.x }
        val searcher = Point()

        for (home in homeCandidates) {
            if (!remainingPixels.contains(home)) continue
            
            val path = mutableListOf<Point>()
            val stack = mutableListOf<Point>()
            var currentPoint = home
            var previousPoint: Point? = null

            while (true) {
                path.add(currentPoint)
                remainingPixels.remove(currentPoint)
                stack.add(currentPoint)

                var nextPoint = findNextStep(currentPoint, previousPoint, remainingPixels, home, path.size, vram, width, searcher)

                if (nextPoint == null) {
                    // --- 180 LOGIC RECOVERY ---
                    nextPoint = findRecoveryPoint(currentPoint, remainingPixels, vram, width)
                }

                if (nextPoint != null) {
                    if (nextPoint == home && path.size > 10) {
                        allPaths.add(path)
                        break
                    }
                    previousPoint = currentPoint
                    currentPoint = nextPoint
                } else {
                    var foundBranch = false
                    if (stack.isNotEmpty()) stack.removeAt(stack.size - 1)

                    while (stack.isNotEmpty()) {
                        val junction = stack.removeAt(stack.size - 1)
                        val branch = findNextStep(junction, null, remainingPixels, home, path.size, vram, width, searcher)
                        if (branch != null) {
                            path.add(Point(-1, -1))
                            previousPoint = junction
                            currentPoint = branch
                            foundBranch = true
                            break
                        }
                    }

                    if (!foundBranch) {
                        if (path.size > 2) allPaths.add(path)
                        break
                    }
                }
            }
        }
        return allPaths
    }

    private fun findNextStep(
        current: Point, 
        previous: Point?, 
        remaining: Set<Point>, 
        home: Point, 
        pathSize: Int, 
        vram: ByteBuffer,
        width: Int,
        searcher: Point
    ): Point? {
        val lastMoveIndex = if (previous != null) {
            val dx = current.x - previous.x
            val dy = current.y - previous.y
            OFFSETS.indexOfFirst { it.x == dx && it.y == dy }
        } else -1
        
        val searchStartIndex = if (lastMoveIndex != -1) (lastMoveIndex + 5) % 8 else 0

        for (i in 0 until 8) {
            val idx = (searchStartIndex + i) % 8
            if (idx % 2 != 0) continue
            val offset = OFFSETS[idx]
            val nx = current.x + offset.x
            val ny = current.y + offset.y
            if (nx == home.x && ny == home.y && pathSize > 10) return home
            if (remaining.contains(Point(nx, ny)) && getBit(vram, ny * width + nx)) return Point(nx, ny)
        }

        for (i in 0 until 8) {
            val idx = (searchStartIndex + i) % 8
            if (idx % 2 == 0) continue
            val offset = OFFSETS[idx]
            val nx = current.x + offset.x
            val ny = current.y + offset.y
            if (nx == home.x && ny == home.y && pathSize > 10) return home
            if (remaining.contains(Point(nx, ny)) && getBit(vram, ny * width + nx)) return Point(nx, ny)
        }
        return null
    }

    private fun findRecoveryPoint(
        current: Point,
        remaining: Set<Point>,
        vram: ByteBuffer,
        width: Int
    ): Point? {
        for (r in 1..3) {
            for (dy in -r..r) {
                for (dx in -r..r) {
                    if (abs(dx) < r && abs(dy) < r) continue
                    val nx = current.x + dx
                    val ny = current.y + dy
                    if (remaining.contains(Point(nx, ny)) && getBit(vram, ny * width + nx)) {
                        return Point(nx, ny)
                    }
                }
            }
        }
        return null
    }

    private fun getBit(buffer: ByteBuffer, index: Int): Boolean {
        val byteIdx = index / 8
        if (byteIdx >= buffer.capacity()) return false
        val bitIdx = index % 8
        return (buffer.get(byteIdx).toInt() and (1 shl bitIdx)) != 0
    }
}
