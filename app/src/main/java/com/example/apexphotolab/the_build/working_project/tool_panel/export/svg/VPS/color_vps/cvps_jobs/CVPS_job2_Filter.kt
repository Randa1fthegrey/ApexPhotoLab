package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import java.nio.ByteBuffer

/**
 * Job: CVPS Job 2 Specialist - VRAM Distiller.
 * Responsibility: Shaving "dirtied up" ribbons into a perfect 1-pixel skeleton using Zhang-Suen Thinning.
 */
object CVPS_job2_Filter {

    fun execute(vram: ByteBuffer, width: Int, height: Int, edges: HashSet<Point>) {
        var changed = true
        var safety = 0

        // Zhang-Suen 1-Pixel Skeletonization
        while (changed && safety < 10) {
            changed = false
            safety++

            // SUB-PASS 1
            val toRemove1 = mutableListOf<Point>()
            for (p in edges) {
                if (shouldRemoveZhangSuen(p, vram, width, height, subPass = 1)) {
                    toRemove1.add(p)
                }
            }
            if (toRemove1.isNotEmpty()) {
                changed = true
                for (p in toRemove1) {
                    CVPS_VRAM_Util.clearBit(vram, p.y * width + p.x)
                    edges.remove(p)
                }
            }

            // SUB-PASS 2
            val toRemove2 = mutableListOf<Point>()
            for (p in edges) {
                if (shouldRemoveZhangSuen(p, vram, width, height, subPass = 2)) {
                    toRemove2.add(p)
                }
            }
            if (toRemove2.isNotEmpty()) {
                changed = true
                for (p in toRemove2) {
                    CVPS_VRAM_Util.clearBit(vram, p.y * width + p.x)
                    edges.remove(p)
                }
            }
        }
    }

    private fun shouldRemoveZhangSuen(p: Point, vram: ByteBuffer, w: Int, h: Int, subPass: Int): Boolean {
        // 8-neighbors in clockwise order: N, NE, E, SE, S, SW, W, NW
        val offsets = arrayOf(
            Point(0, -1), Point(1, -1), Point(1, 0), Point(1, 1),
            Point(0, 1), Point(-1, 1), Point(-1, 0), Point(-1, -1)
        )

        val n = BooleanArray(8)
        var count = 0
        for (i in 0 until 8) {
            val nx = p.x + offsets[i].x
            val ny = p.y + offsets[i].y
            if (nx in 0 until w && ny in 0 until h && CVPS_VRAM_Util.getBit(vram, ny * w + nx)) {
                n[i] = true
                count++
            }
        }

        // Condition 1: 2 <= N(P) <= 6 (not isolated, not endpoint, not interior)
        if (count < 2 || count > 6) return false

        // Condition 2: S(P) == 1 (0-to-1 transitions clockwise)
        var transitions = 0
        for (i in 0 until 8) {
            if (!n[i] && n[(i + 1) % 8]) transitions++
        }
        if (transitions != 1) return false

        // Conditions 3 & 4 (Zhang-Suen directional sub-pass checks)
        val p2 = n[0]
        val p4 = n[2]
        val p6 = n[4]
        val p8 = n[6]

        return if (subPass == 1) {
            (!p2 || !p4 || !p6) && (!p4 || !p6 || !p8)
        } else {
            (!p2 || !p4 || !p8) && (!p2 || !p6 || !p8)
        }
    }
}
