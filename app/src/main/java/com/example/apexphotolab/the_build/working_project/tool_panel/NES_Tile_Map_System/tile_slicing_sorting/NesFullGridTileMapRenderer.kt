package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Job: Full 960-Tile Grid Map Renderer.
 * Responsibility: Reconstructs the 256x240 NES screen from the 960 (32x30) tile grid,
 * scales it up 4x for high clarity, and draws grid lines around ALL 960 individual 8x8 tiles
 * for human inspection.
 */
object NesFullGridTileMapRenderer {

    fun renderFullGridMap(
        uniqueTiles: List<IntArray>,
        nametableTileIds: IntArray,
        scale: Int = 4,
        drawGridLines: Boolean = true
    ): Bitmap {
        val baseWidth = 256
        val baseHeight = 240

        val baseBitmap = Bitmap.createBitmap(baseWidth, baseHeight, Bitmap.Config.ARGB_8888)
        val basePixels = IntArray(baseWidth * baseHeight)

        // Render all 960 tiles into their exact 32x30 grid positions
        for (gridIndex in 0 until 960) {
            if (gridIndex >= nametableTileIds.size) break
            val tileId = nametableTileIds[gridIndex]
            val col = gridIndex % 32
            val row = gridIndex / 32

            val tilePixels = if (tileId < uniqueTiles.size) uniqueTiles[tileId] else IntArray(64)

            for (r in 0 until 8) {
                for (c in 0 until 8) {
                    val canvasX = col * 8 + c
                    val canvasY = row * 8 + r
                    val srcIdx = r * 8 + c
                    if (srcIdx < tilePixels.size) {
                        basePixels[canvasY * baseWidth + canvasX] = tilePixels[srcIdx]
                    }
                }
            }
        }

        baseBitmap.setPixels(basePixels, 0, baseWidth, 0, 0, baseWidth, baseHeight)

        if (scale <= 1 && !drawGridLines) {
            return baseBitmap
        }

        // Scale up 4x to 1024x960 px so every tile is crisp and easy to inspect
        val scaledWidth = baseWidth * scale
        val scaledHeight = baseHeight * scale
        val scaledBitmap = Bitmap.createScaledBitmap(baseBitmap, scaledWidth, scaledHeight, false)

        if (drawGridLines) {
            val canvas = Canvas(scaledBitmap)
            val paint = Paint().apply {
                color = Color.DKGRAY
                strokeWidth = 1f
                style = Paint.Style.STROKE
            }

            // Draw grid lines around ALL 960 (32x30) tiles
            for (col in 0..32) {
                val x = (col * 8 * scale).toFloat()
                canvas.drawLine(x, 0f, x, scaledHeight.toFloat(), paint)
            }
            for (row in 0..30) {
                val y = (row * 8 * scale).toFloat()
                canvas.drawLine(0f, y, scaledWidth.toFloat(), y, paint)
            }
        }

        return scaledBitmap
    }
}
