package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.SVG_Unified_Audit
import java.nio.ByteBuffer

/**
 * Job: CVPS Job 2 - TEST Discovery (The Orchestrator).
 * Responsibility: Reversing Anti-Aliasing (Heal then Shave) to produce a 1-pixel machine-ready path.
 */
object CVPS_job2_TEST {

    data class DiscoveryData(
        val edges: HashSet<Point>,
        val vram: ByteBuffer,
        val width: Int,
        val height: Int,
        val pixels: IntArray,
        val sharedRemainingSet: MutableSet<Point>? = null,
        val specificCandidates: List<Point>? = null,
        var result: List<List<Point>> = emptyList()
    )

    fun execute(colorId: Int, data: Any?) {
        val dData = data as? DiscoveryData ?: return
        
        // 1. "DIRTY IT UP" (THE HEALER): Reverse AA gaps by filling small holes
        val healedCount = CVPS_job2_Healer.execute(dData.vram, dData.width, dData.height, dData.edges)
        SVG_Unified_Audit.logTrace("HEAL (Dirtying)", healedCount, countActivePixels(dData.vram, dData.width, dData.height))

        // 2. "CLEAN IT UP" (THE FILTER): Iterative shave to produce a 1-pixel skeleton
        CVPS_job2_Filter.execute(dData.vram, dData.width, dData.height, dData.edges, listOf(mutableSetOf()))
        SVG_Unified_Audit.logTrace("FILTER (Cleaning)", 0, countActivePixels(dData.vram, dData.width, dData.height))

        // 3. "THE ONE TRUE TRACE": Walk the perfect machine-ready wire
        dData.result = CVPS_job2_Tracer.execute(colorId, dData.vram, dData.width, dData.height, dData.pixels)
        SVG_Unified_Audit.logTrace("INSPECT (Final)", dData.result.size, countActivePixels(dData.vram, dData.width, dData.height))
    }

    private fun countActivePixels(vram: ByteBuffer, w: Int, h: Int): Int {
        var count = 0
        for (i in 0 until (w * h)) {
            if (CVPS_VRAM_Util.getBit(vram, i)) count++
        }
        return count
    }
}
