package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.flattening

import android.graphics.Bitmap

/**
 * Job: NES Canvas Recalibration (256 × 240 px).
 * Responsibility: Scales and snaps the unwarped screen bitmap to the exact NES NTSC display resolution:
 * - 256 × 240 pixels total
 * - 32 × 30 grid of 8 × 8 pixel tiles
 */
object NesCanvasRecalibrator {

    const val NES_WIDTH = 256
    const val NES_HEIGHT = 240

    fun recalibrate(unwarpedBitmap: Bitmap): Bitmap {
        if (unwarpedBitmap.width == NES_WIDTH && unwarpedBitmap.height == NES_HEIGHT) {
            return unwarpedBitmap
        }

        // Nearest-Neighbor scale to exact NES NTSC 256x240 display bounds.
        // (filter = false prevents bilinear blurring of sharp 8-bit graphics)
        return Bitmap.createScaledBitmap(unwarpedBitmap, NES_WIDTH, NES_HEIGHT, false)
    }
}
