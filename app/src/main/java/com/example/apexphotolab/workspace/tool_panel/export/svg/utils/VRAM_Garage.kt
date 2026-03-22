package com.example.apexphotolab.workspace.tool_panel.export.svg.utils

import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * The "Reserved Parking Garage" for our custom dispatch engine.
 * Updated: Now includes a Power Wash protocol to prevent memory pollution.
 */
object VRAM_Garage {

    private const val TAG = "VRAM_Garage"
    private const val TOTAL_MANAGERS = 31 // Supports 1-based Manager IDs (1-30)
    private const val SLOT_SIZE_BYTES = 512 * 1024 // 0.5 MB
    private const val GARAGE_SIZE_BYTES = TOTAL_MANAGERS * SLOT_SIZE_BYTES

    private val garage: ByteBuffer = ByteBuffer.allocateDirect(GARAGE_SIZE_BYTES)
    
    // A reusable "Master Zero" array for high-speed memory wiping
    private val powerWashBuffer = ByteArray(SLOT_SIZE_BYTES)

    init {
        garage.order(ByteOrder.LITTLE_ENDIAN)
        Log.d(TAG, "VRAM GARAGE ONLINE. ${GARAGE_SIZE_BYTES / (1024 * 1024)}MB of unified memory forged.")
    }

    /**
     * Returns a sandboxed, private ByteBuffer slice for a specific manager.
     */
    fun getSlotForManager(managerId: Int): ByteBuffer {
        val safeId = managerId.coerceIn(0, TOTAL_MANAGERS - 1)
        val offset = safeId * SLOT_SIZE_BYTES
        
        return (garage.duplicate().apply {
            position(offset)
            limit(offset + SLOT_SIZE_BYTES)
        }).slice()
    }

    /**
     * Truly erases all data in a manager's slot to prevent "Ghost Pixels".
     * position and limit are reset to 0 and capacity respectively.
     */
    fun wipeSlot(managerId: Int) {
        val slot = getSlotForManager(managerId)
        slot.clear()
        slot.put(powerWashBuffer)
        slot.clear()
    }
}
