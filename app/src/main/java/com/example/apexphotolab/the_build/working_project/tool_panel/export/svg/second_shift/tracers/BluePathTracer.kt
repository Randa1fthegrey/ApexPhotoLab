package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ColorWallScale
import java.nio.ByteBuffer
import kotlin.math.abs
object BluePathTracer {
    fun trace(homeCandidates: List<Point>, vram: ByteBuffer, width: Int, pixels: IntArray, remainingPixels: MutableSet<Point>? = null): List<List<Point>> {
        val allPaths = mutableListOf<List<Point>>()
        val myRemaining = remainingPixels ?: homeCandidates.toMutableSet()
        for (home in homeCandidates) {
            if (!myRemaining.contains(home)) continue
            val path = mutableListOf<Point>()
            val stack = mutableListOf<Point>()
            var currentPoint = home
            var previousPoint: Point? = null
            while (true) {
                if (!myRemaining.remove(currentPoint)) break
                path.add(currentPoint)
                stack.add(currentPoint)
                var nextPoint = findNextStep(currentPoint, previousPoint, myRemaining, home, path.size, vram, width, pixels)
                if (nextPoint == null) nextPoint = findRecoveryPoint(currentPoint, myRemaining, vram, width, pixels)
                if (nextPoint != null) {
                    if (nextPoint == home && path.size > 10) { allPaths.add(path); break }
                    previousPoint = currentPoint
                    currentPoint = nextPoint
                } else {
                    var foundBranch = false
                    if (stack.isNotEmpty()) stack.removeAt(stack.size - 1)
                    while (stack.isNotEmpty()) {
                        val junction = stack.removeAt(stack.size - 1)
                        val branch = findNextStep(junction, null, myRemaining, home, path.size, vram, width, pixels)
                        if (branch != null) { path.add(Point(-1, -1)); previousPoint = junction; currentPoint = branch; foundBranch = true; break }
                    }
                    if (!foundBranch) { if (path.size > 2) allPaths.add(path); break }
                }
            }
        }
        return allPaths
    }
    private val OFFSETS = arrayOf(Point(0,-1), Point(1,-1), Point(1,0), Point(1,1), Point(0,1), Point(-1,1), Point(-1,0), Point(-1,-1))
    private fun findNextStep(current: Point, previous: Point?, remaining: Set<Point>, home: Point, pathSize: Int, vram: ByteBuffer, width: Int, pixels: IntArray): Point? {
        val lastMoveIndex = if (previous != null) { val dx = current.x - previous.x; val dy = current.y - previous.y; OFFSETS.indexOfFirst { it.x == dx && it.y == dy } } else -1
        val searchStartIndex = if (lastMoveIndex != -1) (lastMoveIndex + 5) % 8 else 0
        val currentPixel = pixels[current.y * width + current.x]
        for (i in 0 until 8) {
            val idx = (searchStartIndex + i) % 8
            val nx = current.x + OFFSETS[idx].x; val ny = current.y + OFFSETS[idx].y
            if (nx == home.x && ny == home.y && pathSize > 10) return home
            val neighborPoint = Point(nx, ny)
            if (remaining.contains(neighborPoint) && getBit(vram, ny * width + nx)) {
                if (ColorWallScale.isSolidGround(currentPixel, pixels[ny * width + nx])) return neighborPoint
            }
        }
        return null
    }
    private fun findRecoveryPoint(current: Point, remaining: Set<Point>, vram: ByteBuffer, width: Int, pixels: IntArray): Point? {
        val currentPixel = pixels[current.y * width + current.x]
        for (r in 1..3) {
            for (dy in -r..r) {
                for (dx in -r..r) {
                    if (abs(dx) < r && abs(dy) < r) continue
                    val nx = current.x + dx; val ny = current.y + dy
                    if (remaining.contains(Point(nx, ny)) && getBit(vram, ny * width + nx)) {
                        if (ColorWallScale.isSolidGround(currentPixel, pixels[ny * width + nx])) return Point(nx, ny)
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
