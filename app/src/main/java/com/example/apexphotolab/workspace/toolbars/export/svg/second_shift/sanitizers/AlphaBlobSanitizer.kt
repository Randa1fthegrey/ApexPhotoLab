package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.sanitizers

import com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.VRAM_BlobConverter
import java.nio.ByteBuffer

/**
 * Sanitizer for the ALPHA/TRANSPARENT color group.
 * True VRAM Version: Operates directly on bitmasks to ensure zero heap overhead.
 */
object AlphaBlobSanitizer {

    /**
     * Sanitizes the pixel group entirely within VRAM.
     */
    fun sanitizeInPlace(indices: List<Int>, width: Int, height: Int, vram: ByteBuffer) {
        if (indices.isEmpty()) return
        
        // 1. Convert raw indices directly to VRAM Bitmask (Zero Heap)
        VRAM_BlobConverter.convertToVRAM(indices, vram)
        
        // 2. Perform Noise Filtering in VRAM (In-place)
        filterNoiseInPlace(vram, width, height)
        
        // Alpha group skips healing to preserve performance on massive blobs.
    }

    private fun filterNoiseInPlace(vram: ByteBuffer, width: Int, height: Int) {
        val bitsetSize = (width * height + 7) / 8
        if (vram.capacity() < bitsetSize * 2) return
        
        val originalOffset = 0
        val cleanedOffset = bitsetSize
        
        // Clear cleanup buffer
        for (i in cleanedOffset until (cleanedOffset + bitsetSize)) {
            vram.put(i, 0)
        }

        for (y in 0 until height) {
            val rowOffset = y * width
            for (x in 0 until width) {
                if (getBit(vram, originalOffset + rowOffset + x)) {
                    var hasNeighbor = false
                    neighborSearch@for (dy in -1..1) {
                        for (dx in -1..1) {
                            if (dx == 0 && dy == 0) continue
                            val nx = x + dx
                            val ny = y + dy
                            if (nx in 0 until width && ny in 0 until height) {
                                if (getBit(vram, originalOffset + ny * width + nx)) {
                                    hasNeighbor = true
                                    break@neighborSearch
                                }
                            }
                        }
                    }
                    if (hasNeighbor) {
                        setBit(vram, cleanedOffset + rowOffset + x)
                    }
                }
            }
        }
        
        // Copy cleaned back to original slot
        for (i in 0 until bitsetSize) {
            vram.put(i, vram.get(cleanedOffset + i))
        }
    }

    private fun setBit(buffer: ByteBuffer, index: Int) {
        val byteIdx = index / 8
        if (byteIdx >= buffer.capacity()) return
        val bitIdx = index % 8
        buffer.put(byteIdx, (buffer.get(byteIdx).toInt() or (1 shl bitIdx)).toByte())
    }

    private fun getBit(buffer: ByteBuffer, index: Int): Boolean {
        val byteIdx = index / 8
        if (byteIdx >= buffer.capacity()) return false
        val bitIdx = index % 8
        return (buffer.get(byteIdx).toInt() and (1 shl bitIdx)) != 0
    }
}
