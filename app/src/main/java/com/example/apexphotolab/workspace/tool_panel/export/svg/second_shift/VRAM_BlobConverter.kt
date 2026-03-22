package com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift

import android.graphics.Point
import java.nio.ByteBuffer

/**
 * Job #2: The VRAM Conversion Department.
 * Populates a VRAM bitmask directly from raw pixel indices or Point sets.
 * Prevents heap-based OOMs by avoiding excessive object creation.
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
            if (byteIdx < vram.capacity()) {
                val bitIdx = idx % 8
                val currentByte = vram.get(byteIdx).toInt()
                vram.put(byteIdx, (currentByte or (1 shl bitIdx)).toByte())
            }
        }
    }

    /**
     * Fills the provided VRAM buffer with a bitmask from a set of Points.
     */
    fun convertToVRAM(blob: HashSet<Point>, vram: ByteBuffer, width: Int) {
        vram.clear()
        blob.forEach { p ->
            val idx = p.y * width + p.x
            val byteIdx = idx / 8
            if (byteIdx < vram.capacity()) {
                val bitIdx = idx % 8
                val currentByte = vram.get(byteIdx).toInt()
                vram.put(byteIdx, (currentByte or (1 shl bitIdx)).toByte())
            }
        }
    }
}
