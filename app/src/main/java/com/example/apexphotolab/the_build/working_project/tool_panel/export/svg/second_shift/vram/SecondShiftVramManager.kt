package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.VRAM_Garage
import java.nio.ByteBuffer

/**
 * Job: Second Shift VRAM Manager.
 * Responsibility: Managing the allocation, wiping, and bit-level manipulation of VRAM slots.
 */
object SecondShiftVramManager {

    private const val MASTER_SLOT = 0

    fun prepareMasterVram(): ByteBuffer {
        VRAM_Garage.wipeSlot(MASTER_SLOT)
        return VRAM_Garage.getSlotForManager(MASTER_SLOT)
    }

    fun getMasterVram(): ByteBuffer {
        return VRAM_Garage.getSlotForManager(MASTER_SLOT)
    }

    fun setEdgeBit(buffer: ByteBuffer, index: Int) {
        val byteIdx = index / 8
        if (byteIdx < buffer.capacity()) {
            val bitIdx = index % 8
            val currentByte = buffer.get(byteIdx).toInt()
            buffer.put(byteIdx, (currentByte or (1 shl bitIdx)).toByte())
        }
    }
}