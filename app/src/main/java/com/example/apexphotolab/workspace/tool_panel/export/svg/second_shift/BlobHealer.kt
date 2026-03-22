package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift

import java.nio.ByteBuffer

/**
 * A specialized tool for the Second Shift.
 * Performs high-performance morphological operations to heal "speckled" blobs.
 * True VRAM Version: Operates purely on bitmasks to ensure zero heap overhead.
 */
object BlobHealer {

    /**
     * Fills gaps and holes using a 2-pixel radius Morphological Closing.
     * Operates directly on the VRAM buffer to prevent OutOfMemoryErrors.
     */
    fun healInPlaceVRAM(width: Int, height: Int, vram: ByteBuffer) {
        val bitsetSize = (width * height + 7) / 8
        if (vram.capacity() < bitsetSize * 2) return

        val gridOffset = 0
        val dilatedOffset = bitsetSize

        // 1. Dilate: Expand by 2 pixels from gridOffset to dilatedOffset
        // First, clear the dilated buffer
        for (i in dilatedOffset until (dilatedOffset + bitsetSize)) {
            if (i < vram.capacity()) vram.put(i, 0)
        }

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (getBit(vram, gridOffset + y * width + x)) {
                    for (dy in -2..2) {
                        val ny = y + dy
                        if (ny !in 0 until height) continue
                        for (dx in -2..2) {
                            val nx = x + dx
                            if (nx in 0 until width) {
                                setBit(vram, dilatedOffset + ny * width + nx)
                            }
                        }
                    }
                }
            }
        }

        // 2. Erode: Shrink back by 2 pixels from dilatedOffset back to gridOffset
        // First, clear the original grid buffer
        for (i in 0 until bitsetSize) {
            vram.put(i, 0)
        }

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (getBit(vram, dilatedOffset + y * width + x)) {
                    var isSolid = true
                    checkNeighborhood@for (dy in -2..2) {
                        val ny = y + dy
                        if (ny !in 0 until height) continue
                        for (dx in -2..2) {
                            val nx = x + dx
                            if (nx !in 0 until width) continue
                            if (!getBit(vram, dilatedOffset + ny * width + nx)) {
                                isSolid = false
                                break@checkNeighborhood
                            }
                        }
                    }
                    
                    if (isSolid) {
                        setBit(vram, gridOffset + y * width + x)
                    }
                }
            }
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
