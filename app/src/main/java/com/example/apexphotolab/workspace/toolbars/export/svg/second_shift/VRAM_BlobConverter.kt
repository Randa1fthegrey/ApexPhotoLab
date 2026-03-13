package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift

import java.nio.ByteBuffer

/**
 * Job #2: The VRAM Conversion Department.
 * Populates a VRAM bitmask directly from raw pixel indices.
 * Prevents heap-based OOMs by avoiding HashSet<Point> creation.
 */
object VRAM_BlobConverter {

    /**
     * Fills the provided VRAM buffer with a bitmask of the pixel group.
     * @param indices Flat pixel indices from the image.
     * @param vram The dedicated ByteBuffer slot for this color group.
     */
    fun convertToVRAM(indices: List<Int>, vram: ByteBuffer) {
        vram.clear()
        indices.forEach { idx ->
            val byteIdx = idx / 8
            // Safety check against slot capacity
            if (byteIdx < vram.capacity()) {
                val bitIdx = idx % 8
                val currentByte = vram.get(byteIdx).toInt()
                vram.put(byteIdx, (currentByte or (1 shl bitIdx)).toByte())
            }
        }
    }
}
