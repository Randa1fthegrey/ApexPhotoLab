package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram

import android.graphics.Point
import java.nio.ByteBuffer

/**
 * Job: VRAM Blob Converter.
 * Responsibility: Populating a VRAM bitmask from raw pixel indices or Point sets
 * to prevent heap-based OOM crashes.
 */
object VRAM_BlobConverter {

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