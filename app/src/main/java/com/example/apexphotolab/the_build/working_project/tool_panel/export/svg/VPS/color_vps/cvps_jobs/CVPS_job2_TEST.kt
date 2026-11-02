package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
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
        CVPS_job2_Healer.execute(dData.vram, dData.width, dData.height, dData.edges)

        // 2. "CLEAN IT UP" (THE FILTER): Iterative shave to produce a 1-pixel skeleton
        CVPS_job2_Filter.execute(dData.vram, dData.width, dData.height, dData.edges)

        // 3. "THE ONE TRUE TRACE": Walk the perfect machine-ready wire
        dData.result = CVPS_job2_Tracer.execute(colorId, dData.vram, dData.width, dData.height, dData.pixels)
    }
}
