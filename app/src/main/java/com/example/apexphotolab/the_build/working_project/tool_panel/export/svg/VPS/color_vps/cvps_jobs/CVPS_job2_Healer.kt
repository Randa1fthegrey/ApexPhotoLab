package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import java.nio.ByteBuffer

/**
 * Job: CVPS Job 2 Specialist - Healer.
 * Responsibility: "Dirtying up" the image to reverse Anti-Aliasing gaps.
 * Logic: If a pixel is NOT an edge but has Grey neighbors on opposite sides, 
 * it is a gap that needs to be filled (Claimed for GREY).
 */
object CVPS_job2_Healer {

    fun execute(
        vram: ByteBuffer,
        width: Int,
        height: Int,
        allEdges: HashSet<Point>
    ): Int {
        val newEdges = mutableListOf<Point>()
        
        // Scan the entire bounding box area or image
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val idx = y * width + x
                if (CVPS_VRAM_Util.getBit(vram, idx)) continue
                
                // Gap Detection Patterns (Undo AA) - 1-pixel and 2-pixel gaps
                val n1 = CVPS_VRAM_Util.getBit(vram, (y - 1) * width + x)
                val s1 = CVPS_VRAM_Util.getBit(vram, (y + 1) * width + x)
                val e1 = CVPS_VRAM_Util.getBit(vram, y * width + (x + 1))
                val w1 = CVPS_VRAM_Util.getBit(vram, y * width + (x - 1))
                
                val n2 = if (y > 1) CVPS_VRAM_Util.getBit(vram, (y - 2) * width + x) else false
                val s2 = if (y < height - 2) CVPS_VRAM_Util.getBit(vram, (y + 2) * width + x) else false
                val e2 = if (x < width - 2) CVPS_VRAM_Util.getBit(vram, y * width + (x + 2)) else false
                val w2 = if (x > 1) CVPS_VRAM_Util.getBit(vram, y * width + (x - 2)) else false

                // If we are between two Grey pixels (1 or 2 steps away), heal the bridge
                if ((n1 && s1) || (e1 && w1) || (n1 && s2) || (s1 && n2) || (e1 && w2) || (w1 && e2)) {
                    newEdges.add(Point(x, y))
                }
            }
        }

        for (p in newEdges) {
            CVPS_VRAM_Util.setBit(vram, p.y * width + p.x)
            allEdges.add(p)
        }
        
        return newEdges.size
    }
}
