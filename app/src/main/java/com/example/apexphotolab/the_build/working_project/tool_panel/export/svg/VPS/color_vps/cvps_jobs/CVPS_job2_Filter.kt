package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.val_util as global_val_util
import java.nio.ByteBuffer

/**
 * Job: CVPS Job 2 Specialist - VRAM Distiller.
 * Responsibility: Shaving "dirtied up" ribbons into a perfect 1-pixel skeleton.
 */
object CVPS_job2_Filter {

    fun execute(vram: ByteBuffer, width: Int, height: Int, allEdges: HashSet<Point>, bucketCandidates: List<MutableSet<Point>>) {
        var changed = true
        var safety = 0
        
        // Iterative Thinning: Keep shaving until only a 1-pixel skeleton remains
        while (changed && safety < 5) {
            changed = false
            safety++
            val toRemove = mutableListOf<Point>()

            for (p in allEdges) {
                val neighborStates = BooleanArray(8)
                var neighborsOn = 0
                for (i in 0 until 8) {
                    val offset = global_val_util.OFFSETS[i]
                    val nx = p.x + offset.x
                    val ny = p.y + offset.y
                    if (nx in 0 until width && ny in 0 until height && CVPS_VRAM_Util.getBit(vram, ny * width + nx)) {
                        neighborStates[i] = true
                        neighborsOn++
                    }
                }

                // 1. INTERIOR RULE: Delete if fully surrounded
                if (neighborsOn == 8) {
                    toRemove.add(p)
                    continue
                }

                // 2. JUNCTION PRUNING:
                // If a pixel has 3 or more neighbors, it's a branch/junction.
                // We delete it to force a single, clean chain.
                if (neighborsOn >= 3) {
                    toRemove.add(p); changed = true
                    continue
                }

                // 3. SKELETON RULE: Shave redundant corners
                if (neighborStates[0] && neighborStates[2] && neighborStates[1]) {
                    toRemove.add(p); changed = true
                } else if (neighborStates[2] && neighborStates[4] && neighborStates[3]) {
                    toRemove.add(p); changed = true
                } else if (neighborStates[4] && neighborStates[6] && neighborStates[5]) {
                    toRemove.add(p); changed = true
                } else if (neighborStates[7] && neighborStates[0] && neighborStates[6]) {
                    toRemove.add(p); changed = true
                }
            }

            for (p in toRemove) {
                CVPS_VRAM_Util.clearBit(vram, p.y * width + p.x)
                allEdges.remove(p)
                for (bucket in bucketCandidates) bucket.remove(p)
            }
        }
    }
}
