package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift

import android.graphics.Point
import com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.sanitizers.*

/**
 * Job #3: The Sanitization Switchboard.
 * Routes each color group to its specialized, VRAM-aware sanitizer.
 * This prevents the "God Object" bottleneck and eliminates heap-based OOM crashes.
 */
object SanitizationRouter {

    /**
     * Delegates sanitization to the correct specialist.
     * Each specialist uses a dedicated VRAM slot for bitmask operations.
     */
    fun sanitize(index: Int, blob: HashSet<Point>, width: Int, height: Int): HashSet<Point> {
        return when (index) {
            0 -> RedBlobSanitizer.sanitize(blob, width, height)
            1 -> GreenBlobSanitizer.sanitize(blob, width, height)
            2 -> BlueBlobSanitizer.sanitize(blob, width, height)
            3 -> YellowBlobSanitizer.sanitize(blob, width, height)
            4 -> CyanBlobSanitizer.sanitize(blob, width, height)
            5 -> MagentaBlobSanitizer.sanitize(blob, width, height)
            6 -> WhiteBlobSanitizer.sanitize(blob, width, height)
            7 -> blob // Alpha group bypasses this legacy route.
            8 -> BlackBlobSanitizer.sanitize(blob, width, height)
            9 -> GreyBlobSanitizer.sanitize(blob, width, height)
            else -> GreyBlobSanitizer.sanitize(blob, width, height)
        }
    }
}
