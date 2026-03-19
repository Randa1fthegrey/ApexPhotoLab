package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift

import java.nio.ByteBuffer

/**
 * A specialized tool for the Second Shift.
 * Performs high-speed noise filtering directly on VRAM bitmasks.
 */
object VRAM_NoiseFilter {

    /**
     * Removes "speckles" (pixels with no neighbors) from the VRAM bitmask.
     * Uses a secondary slot in the buffer to perform the operation in-place.
     */
    fun filterInPlace(vram: ByteBuffer, width: Int, height: Int) {
        val bitsetSize = (width * height + 7) / 8
        if (vram.capacity() < bitsetSize * 2) return
        
        val originalOffset = 0
        val cleanedOffset = bitsetSize
        
        // 1. Clear the "Work Area" (the second half of the buffer slot)
        for (i in cleanedOffset until (cleanedOffset + bitsetSize)) {
            vram.put(i, 0)
        }

        // 2. Scan the original and keep only pixels that have at least one neighbor
        for (y in 0 until height) {
            val rowOffset = y * width
            for (x in 0 until width) {
                if (getBit(vram, originalOffset + rowOffset + x)) {
                    var hasNeighbor = false
                    
                    // Check 8-way neighbors
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
        
        // 3. Move the cleaned result back to the primary offset
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
