package com.example.apexphotolab.workspace.toolbars.export.svg.first_shift

import java.nio.ByteBuffer

/**
 * A common interface for all First Shift workers.
 * This defines the contract for a VRAM-aware parallel worker.
 */
interface FirstShiftWorker {
    val id: Int
    suspend fun processSlice(
        sourcePixels: IntArray,
        targetPixels: IntArray,
        slice: IntRange,
        vramSlot: ByteBuffer // The manager's private VRAM work area
    )
}