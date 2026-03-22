package com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift

import android.graphics.Bitmap
import android.graphics.Point
import java.nio.ByteBuffer

/**
 * The blueprint for a VRAM-aware Third Shift worker (a "Census Taker").
 */
interface ThirdShiftCensusTaker {
    val id: Int
    suspend fun analyzePath(
        path: List<Point>,
        quantizedImage: Bitmap,
        vramSlot: ByteBuffer
    ): Int
}
