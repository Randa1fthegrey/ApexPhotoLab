package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting

import android.graphics.Bitmap

/**
 * Job: Human-Viewable NES Tile Map Renderer.
 * Responsibility: Reconstructs a 256x240 viewable PNG/Bitmap of the tile map
 * using the unique 8x8 CHR tile list and 32x30 Nametable grid for human inspection.
 */
object NesTileMapRenderer {

    fun renderTileMap(
        uniqueTiles: List<IntArray>,
        nametableTileIds: ByteArray
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(256, 240, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(256 * 240)

        for (gridIndex in 0 until 960) {
            if (gridIndex >= nametableTileIds.size) break
            val tileId = nametableTileIds[gridIndex].toInt() and 0xFF
            val col = gridIndex % 32
            val row = gridIndex / 32

            val tilePixels = if (tileId < uniqueTiles.size) uniqueTiles[tileId] else IntArray(64)

            for (tileRow in 0 until 8) {
                for (tileCol in 0 until 8) {
                    val canvasX = col * 8 + tileCol
                    val canvasY = row * 8 + tileRow
                    val pixelColor = tilePixels[tileRow * 8 + tileCol]
                    pixels[canvasY * 256 + canvasX] = pixelColor
                }
            }
        }

        bitmap.setPixels(pixels, 0, 256, 0, 0, 256, 240)
        return bitmap
    }
}
