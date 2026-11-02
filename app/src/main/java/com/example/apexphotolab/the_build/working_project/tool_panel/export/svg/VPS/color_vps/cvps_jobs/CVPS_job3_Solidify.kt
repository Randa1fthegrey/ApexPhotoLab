package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census.CensusReport
import java.nio.ByteBuffer

/**
 * Job: CVPS Job 3 - Solidification (The Orchestrator).
 * Responsibility: Coordinating specialized stitchers and welders to produce watertight paths.
 */
object CVPS_job3_Solidify {

    data class SolidifyData(
        val fragments: List<List<Point>>,
        val reports: List<CensusReport>,
        val vram: ByteBuffer,
        val width: Int,
        val height: Int,
        val pixels: IntArray,
        var result: Pair<List<List<Point>>, List<CensusReport>>? = null
    )

    fun execute(colorId: Int, data: Any?) {
        val sData = data as? SolidifyData ?: return
        
        // 1. STITCHING: Physically bridge gaps and close loops
        val stitched = CVPS_job3_Stitcher.execute(colorId, sData.fragments, sData.vram, sData.width, sData.height, sData.pixels)
        
        // 2. WELDING: Spatially group disconnected elements for all color IDs
        val finalResult = CVPS_job3_Welder.execute(colorId, stitched, sData.reports)
        
        sData.result = finalResult
    }
}
