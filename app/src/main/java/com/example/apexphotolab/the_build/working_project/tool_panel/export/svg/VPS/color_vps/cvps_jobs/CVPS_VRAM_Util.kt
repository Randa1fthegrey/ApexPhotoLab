package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import java.nio.ByteBuffer

/**
 * Job: CVPS VRAM Utility.
 * Responsibility: Providing standardized, high-speed bit manipulation for the Software GPU's VRAM.
 */
object CVPS_VRAM_Util {

    fun getBit(buffer: ByteBuffer, index: Int): Boolean {
        val byteIdx = index / 8
        if (byteIdx >= buffer.capacity()) return false
        val bitIdx = index % 8
        return (buffer.get(byteIdx).toInt() and (1 shl bitIdx)) != 0
    }

    fun setBit(buffer: ByteBuffer, index: Int) {
        val byteIdx = index / 8
        if (byteIdx >= buffer.capacity()) return
        val bitIdx = index % 8
        val currentByte = buffer.get(byteIdx).toInt()
        buffer.put(byteIdx, (currentByte or (1 shl bitIdx)).toByte())
    }

    fun clearBit(buffer: ByteBuffer, index: Int) {
        val byteIdx = index / 8
        if (byteIdx >= buffer.capacity()) return
        val bitIdx = index % 8
        val currentByte = buffer.get(byteIdx).toInt()
        buffer.put(byteIdx, (currentByte and (1 shl bitIdx).inv()).toByte())
    }
}
