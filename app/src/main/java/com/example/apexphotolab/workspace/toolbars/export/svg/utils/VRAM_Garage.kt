package com.example.apexphotolab.workspace.toolbars.export.svg.utils

import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * The "Reserved Parking Garage" for our custom dispatch engine.
 * Updated: Supports 1-based Manager IDs (1-30) safely.
 */
object VRAM_Garage {

    private const val TAG = "VRAM_Garage"
    private const val TOTAL_MANAGERS = 31 // Increased to 31 to safely map 1-based IDs
    private const val SLOT_SIZE_BYTES = 512 * 1024 // 0.5 MB
    private const val GARAGE_SIZE_BYTES = TOTAL_MANAGERS * SLOT_SIZE_BYTES

    private val garage: ByteBuffer = ByteBuffer.allocateDirect(GARAGE_SIZE_BYTES)

    init {
        garage.order(ByteOrder.LITTLE_ENDIAN)
        Log.d(TAG, "VRAM GARAGE ONLINE. ${GARAGE_SIZE_BYTES / (1024 * 1024)}MB of unified memory forged.")
    }

    /**
     * Returns a sandboxed, private ByteBuffer slice for a specific manager.
     * Uses managerId as the direct index.
     */
    fun getSlotForManager(managerId: Int): ByteBuffer {
        // Safety check to prevent out-of-bounds access
        val safeId = managerId.coerceIn(0, TOTAL_MANAGERS - 1)
        val offset = safeId * SLOT_SIZE_BYTES
        
        return (garage.duplicate().apply {
            position(offset)
            limit(offset + SLOT_SIZE_BYTES)
        }).slice()
    }
}
