package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.sanitizers

import android.graphics.Point
import com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.BlobHealer
import com.example.apexphotolab.workspace.toolbars.export.svg.utils.VRAM_Garage
import java.nio.ByteBuffer

/**
 * Sanitizer for the BLACK color group.
 * VRAM-Powered: Uses off-heap memory bitmask for high-speed noise filtering and healing.
 */
object BlackBlobSanitizer {

    private const val ID = 8

    fun sanitize(blob: HashSet<Point>, width: Int, height: Int): HashSet<Point> {
        if (blob.isEmpty()) return blob
        val vram = VRAM_Garage.getSlotForManager(ID)
        
        val cleaned = filterNoise(blob, vram, width, height)
        BlobHealer.healInPlaceVRAM(width, height, vram)
        return cleaned
    }

    private fun filterNoise(blob: HashSet<Point>, vram: ByteBuffer, width: Int, height: Int): HashSet<Point> {
        vram.clear()
        for (p in blob) {
            val idx = p.y * width + p.x
            if (idx / 8 < vram.capacity()) {
                val byteIdx = idx / 8
                val bitIdx = idx % 8
                vram.put(byteIdx, (vram.get(byteIdx).toInt() or (1 shl bitIdx)).toByte())
            }
        }

        val cleaned = HashSet<Point>(blob.size)
        for (p in blob) {
            var hasNeighbor = false
            neighborSearch@for (dy in -1..1) {
                for (dx in -1..1) {
                    if (dx == 0 && dy == 0) continue
                    val nx = p.x + dx
                    val ny = p.y + dy
                    if (nx in 0 until width && ny in 0 until height) {
                        val idx = ny * width + nx
                        val byteIdx = idx / 8
                        val bitIdx = idx % 8
                        if (byteIdx < vram.capacity() && (vram.get(byteIdx).toInt() and (1 shl bitIdx)) != 0) {
                            hasNeighbor = true
                            break@neighborSearch
                        }
                    }
                }
            }
            if (hasNeighbor) cleaned.add(p)
        }
        return cleaned
    }
}
