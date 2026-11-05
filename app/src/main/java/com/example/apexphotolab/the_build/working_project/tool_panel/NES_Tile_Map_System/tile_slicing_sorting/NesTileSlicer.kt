package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting

import android.graphics.Bitmap

/**
 * Job: NES 8x8 Tile Slicer.
 * Responsibility: Slices a 256x240 NES canvas bitmap into 960 8x8 pixel tile blocks (32 columns x 30 rows).
 */
object NesTileSlicer {

    data class RawTile(
        val col: Int,
        val row: Int,
        val pixels: IntArray // 64 pixels (8x8)
    )

    fun slice(bitmap: Bitmap): List<RawTile> {
        val tiles = mutableListOf<RawTile>()
        val width = bitmap.width
        val height = bitmap.height

        val cols = minOf(32, width / 8)
        val rows = minOf(30, height / 8)

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val tilePixels = IntArray(64)
                bitmap.getPixels(tilePixels, 0, 8, col * 8, row * 8, 8, 8)
                tiles.add(RawTile(col, row, tilePixels))
            }
        }
        return tiles
    }
}
