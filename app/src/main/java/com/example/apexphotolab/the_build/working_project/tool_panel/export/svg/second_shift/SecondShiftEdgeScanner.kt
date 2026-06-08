package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift

import android.graphics.Point
import java.nio.ByteBuffer

/**
 * Job: Second Shift Edge Scanner.
 * Responsibility: Scanning the image for Territory boundaries and marking the "Tracks" in VRAM.
 * Strict Perimeter Logic: Only flags pixels that represent a transition between color groups.
 */
object SecondShiftEdgeScanner {

    fun scan(
        width: Int,
        height: Int,
        pixels: IntArray,
        claimedIndices: Set<Int>,
        vram: ByteBuffer
    ): Pair<List<MutableSet<Point>>, HashSet<Point>> {
        
        val bucketCandidates = List(10) { mutableSetOf<Point>() }
        val allEdges = HashSet<Point>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                val currentIdx = y * width + x
                if (claimedIndices.contains(currentIdx)) continue

                val currentGroup = SecondShiftNoiseFilter.getCleanGroup(pixels[currentIdx])
                
                // 2-Way Scan (East and South) is enough to find all boundaries
                checkNeighbor(x + 1, y, currentIdx, currentGroup, width, height, pixels, claimedIndices, vram, bucketCandidates, allEdges)
                checkNeighbor(x, y + 1, currentIdx, currentGroup, width, height, pixels, claimedIndices, vram, bucketCandidates, allEdges)
            }
        }

        return Pair(bucketCandidates, allEdges)
    }

    private fun checkNeighbor(
        nx: Int, ny: Int,
        currentIdx: Int, currentGroup: Int,
        width: Int, height: Int,
        pixels: IntArray, claimedIndices: Set<Int>,
        vram: ByteBuffer,
        bucketCandidates: List<MutableSet<Point>>,
        allEdges: HashSet<Point>
    ) {
        if (nx >= width || ny >= height) return
        
        val neighborIdx = ny * width + nx
        if (claimedIndices.contains(neighborIdx)) return

        val neighborGroup = SecondShiftNoiseFilter.getCleanGroup(pixels[neighborIdx])

        // BOUNDARY DETECTED
        if (currentGroup != neighborGroup) {
            SecondShiftVramManager.setEdgeBit(vram, currentIdx)
            SecondShiftVramManager.setEdgeBit(vram, neighborIdx)
            
            val p1 = Point(currentIdx % width, currentIdx / width)
            val p2 = Point(nx, ny)
            
            allEdges.add(p1)
            allEdges.add(p2)
            
            if (currentGroup in 0..9) bucketCandidates[currentGroup].add(p1)
            if (neighborGroup in 0..9) bucketCandidates[neighborGroup].add(p2)
        }
    }
}
