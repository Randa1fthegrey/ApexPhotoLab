package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.dispatchers

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.territory.ColorWallScale
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure.SecondShiftNoiseFilter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram.SecondShiftVramManager
import java.nio.ByteBuffer

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.val_util as global_val_util

/**
 * Job: Second Shift Edge Scanner.
 * Responsibility: Scanning the image for Territory boundaries and marking the "Tracks" in VRAM.
 * Distillation Logic: Only flags pixels that have at least one neighbor of a DIFFERENT color.
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

                val currentPixel = pixels[currentIdx]
                val currentGroup = SecondShiftNoiseFilter.getCleanGroup(currentPixel)
                
                var isEdge = false
                
                // SIMILARITY COMFORT CHECK: 
                // A pixel is ONLY an edge if at least one of its 8 neighbors is NOT similar.
                for (offset in global_val_util.OFFSETS) {
                    val nx = x + offset.x
                    val ny = y + offset.y
                    
                    if (nx !in 0 until width || ny !in 0 until height) {
                        isEdge = true // Image boundary is the ultimate Abyss
                        break
                    }
                    
                    val neighborIdx = ny * width + nx
                    val isSimilar = ColorWallScale.isSolidGround(currentPixel, pixels[neighborIdx])
                    
                    if (!isSimilar) {
                        isEdge = true
                        break
                    }
                }

                if (isEdge && currentGroup == 9) {
                    SecondShiftVramManager.setEdgeBit(vram, currentIdx)
                    val p = Point(x, y)
                    allEdges.add(p)
                    bucketCandidates[currentGroup].add(p)
                }
            }
        }

        return Pair(bucketCandidates, allEdges)
    }
}
