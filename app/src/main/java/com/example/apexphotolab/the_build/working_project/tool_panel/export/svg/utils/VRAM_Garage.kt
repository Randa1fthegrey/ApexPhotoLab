package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils

import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Job: VRAM Garage.
 * Responsibility: Allocating and managing sandboxed memory slots for each color worker.
 */
object VRAM_Garage {

    fun getSlotForManager(managerId: Int): ByteBuffer {
        val safeId = managerId.coerceIn(0, TOTAL_WORKERS - 1)
        val offset = safeId * SLOT_SIZE_BYTES

        return (garage.duplicate().apply {
            position(offset)
            limit(offset + SLOT_SIZE_BYTES)
        }).slice()
    }

    fun wipeSlot(managerId: Int) {
        val slot = getSlotForManager(managerId)
        slot.clear()
        slot.put(powerWashBuffer)
        slot.clear()
    }

    private val garage: ByteBuffer = ByteBuffer.allocateDirect(GARAGE_SIZE_BYTES)
    private val powerWashBuffer = ByteArray(SLOT_SIZE_BYTES)

    init {
        garage.order(ByteOrder.LITTLE_ENDIAN)
        Log.d(TAG, "VRAM GARAGE ONLINE. ${GARAGE_SIZE_BYTES / (1024 * 1024)}MB of unified memory forged.")
    }

    private const val TAG = "VRAM_Garage"
    private const val TOTAL_WORKERS = 31 // Supports 1-based ColorWorker IDs (1-30)
    private const val SLOT_SIZE_BYTES = 512 * 1024 // 0.5 MB
    private const val GARAGE_SIZE_BYTES = TOTAL_WORKERS * SLOT_SIZE_BYTES
}