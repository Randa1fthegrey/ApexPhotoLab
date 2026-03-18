package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift

import android.graphics.Point
import com.example.apexphotolab.workspace.toolbars.export.svg.utils.VRAM_Garage

/**
 * Job #3: The Sanitization Department.
 * Clean up noise and ensure solid shapes through density filtering and targeted healing.
 * Updated: Legacy wrapper that now uses VRAM-safe logic.
 */
object BlobSanitizer {

    /**
     * Filters noise speckles and heals solid shapes.
     * Memory-Safe Update: Uses VRAM bitmasking to prevent OOM crashes.
     */
    fun sanitize(index: Int, blob: HashSet<Point>, width: Int, height: Int): HashSet<Point> {
        if (blob.isEmpty()) return blob
        
        // Grab a temporary VRAM slot for the operation
        val vram = VRAM_Garage.getSlotForManager(index)

        // 1. Noise Filter (In-place search)
        val cleaned = HashSet<Point>(blob.size)
        val searcher = Point()
        for (p in blob) {
            var hasNeighbor = false
            for (dy in -1..1) {
                for (dx in -1..1) {
                    if (dx == 0 && dy == 0) continue
                    searcher.set(p.x + dx, p.y + dy)
                    if (blob.contains(searcher)) {
                        hasNeighbor = true
                        break
                    }
                }
                if (hasNeighbor) break
            }
            if (hasNeighbor) cleaned.add(p)
        }

        // 2. Selective Conservative Healing:
        // We skip group 7 (Alpha) because it is too massive and doesn't need healing.
        if (index != 7) {
            BlobHealer.healInPlaceVRAM(width, height, vram)
        }
        
        return cleaned
    }
}
