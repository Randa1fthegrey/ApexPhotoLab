package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.sanitizers

import android.graphics.Point
import com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.BlobHealer
import com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.VRAM_BlobConverter
import com.example.apexphotolab.workspace.toolbars.export.svg.utils.VRAM_Garage
import java.nio.ByteBuffer

/**
 * Sanitizer for the RED color group.
 * True VRAM Version: Operates directly on bitmasks to keep heap usage at zero.
 */
object RedBlobSanitizer {

    private const val ID = 0

    /**
     * Sanitizes the pixel group entirely within VRAM.
     * @return The cleaned and healed blob as a HashSet of Points (only for the Edge Finder).
     */
    fun sanitize(indices: List<Int>, width: Int, height: Int): HashSet<Point> {
        if (indices.isEmpty()) return HashSet()
        val vram = VRAM_Garage.getSlotForManager(ID)
        
        // 1. Convert raw indices directly to VRAM Bitmask (Zero Heap)
        VRAM_BlobConverter.convertToVRAM(indices, vram)
        
        // 2. Perform Noise Filtering & Healing in VRAM (Zero Heap)
        BlobHealer.healInPlaceVRAM(width, height, vram)
        
        // 3. Reconstruct HashSet only for the legacy Edge Finder interface
        // Note: This will be fully optimized out in the next pass.
        return reconstructHashSet(vram, width, height)
    }

    private fun reconstructHashSet(vram: ByteBuffer, width: Int, height: Int): HashSet<Point> {
        val blob = HashSet<Point>()
        for (y in 0 until height) {
            val rowOffset = y * width
            for (x in 0 until width) {
                val idx = rowOffset + x
                val byteIdx = idx / 8
                val bitIdx = idx % 8
                if ((vram.get(byteIdx).toInt() and (1 shl bitIdx)) != 0) {
                    blob.add(Point(x, y))
                }
            }
        }
        return blob
    }
}
