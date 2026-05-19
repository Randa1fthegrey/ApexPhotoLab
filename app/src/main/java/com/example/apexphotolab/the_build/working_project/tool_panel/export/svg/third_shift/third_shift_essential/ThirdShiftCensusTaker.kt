package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.third_shift_essential

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.CensusReport
import java.nio.ByteBuffer

/**
 * Job: Third Shift Census Taker.
 * Responsibility: Contract for all VRAM-aware Third Shift workers that analyze paths
 * and return a CensusReport containing color variation data.
 */
interface ThirdShiftCensusTaker {

    val id: Int
    suspend fun analyzePath(
        path: List<Point>,
        quantizedImage: Bitmap,
        vramSlot: ByteBuffer
    ): CensusReport
}