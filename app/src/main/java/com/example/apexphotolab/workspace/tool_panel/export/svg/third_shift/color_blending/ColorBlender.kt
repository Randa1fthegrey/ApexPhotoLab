package com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.color_blending

import android.graphics.Bitmap
import android.graphics.Point
import java.nio.ByteBuffer

/**
 * The blueprint for a VRAM-aware Third Shift worker (a "Census Taker").
 * Updated: Now returns a CensusReport containing color variation data.
 */
interface ThirdShiftCensusTaker {
    val id: Int
    suspend fun analyzePath(
        path: List<Point>,
        quantizedImage: Bitmap,
        vramSlot: ByteBuffer
    ): CensusReport
}

/**
 * The data structure used to report findings for a single shape.
 */
data class CensusReport(
    val dominantColor: Int,
    val totalPixels: Int,
    val blendRatio: Float, // 0.0 (Solid) to 1.0 (Pure Gradient)
    val complexityScore: Int // 0 to 255 based on blendRatio
)
