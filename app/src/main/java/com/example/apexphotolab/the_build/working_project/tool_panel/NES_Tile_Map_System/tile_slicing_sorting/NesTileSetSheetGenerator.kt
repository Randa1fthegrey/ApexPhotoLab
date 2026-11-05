package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Job: Unassembled CHR Tile Sheet Generator.
 * Responsibility: Assembles all unique 8x8 CHR tiles into a side-by-side visual tile sheet (16x16 grid),
 * with pixel scaling (e.g. 4x) and tile grid lines for human inspection.
 */
object NesTileSetSheetGenerator {

    fun generateTileSheet(
        uniqueTiles: List<IntArray>,
        scale: Int = 4,
        drawGridLines: Boolean = true
    ): Bitmap {
        val totalTiles = minOf(256, maxOf(1, uniqueTiles.size))
        val gridCols = 16
        val gridRows = (totalTiles + gridCols - 1) / gridCols // Up to 16 rows

        val baseWidth = gridCols * 8 // 128 px
        val baseHeight = maxOf(1, gridRows) * 8 // Up to 128 px

        val rawSheetPixels = IntArray(baseWidth * baseHeight) { Color.BLACK }

        // Render each 8x8 unique tile into the base sheet grid
        uniqueTiles.forEachIndexed { tileIndex, tilePixels ->
            if (tileIndex >= 256) return@forEachIndexed
            val tileCol = tileIndex % gridCols
            val tileRow = tileIndex / gridCols

            for (r in 0 until 8) {
                for (c in 0 until 8) {
                    val sheetX = tileCol * 8 + c
                    val sheetY = tileRow * 8 + r
                    if (sheetX < baseWidth && sheetY < baseHeight) {
                        val srcIdx = r * 8 + c
                        if (srcIdx < tilePixels.size) {
                            rawSheetPixels[sheetY * baseWidth + sheetX] = tilePixels[srcIdx]
                        }
                    }
                }
            }
        }

        val baseBitmap = Bitmap.createBitmap(baseWidth, baseHeight, Bitmap.Config.ARGB_8888)
        baseBitmap.setPixels(rawSheetPixels, 0, baseWidth, 0, 0, baseWidth, baseHeight)

        if (scale <= 1 && !drawGridLines) {
            return baseBitmap
        }

        // Scale up for easy human viewing (4x scale = 512x512 px)
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

            // Draw tile grid boundaries
            for (col in 0..gridCols) {
                val x = (col * 8 * scale).toFloat()
                canvas.drawLine(x, 0f, x, scaledHeight.toFloat(), paint)
            }
            for (row in 0..gridRows) {
                val y = (row * 8 * scale).toFloat()
                canvas.drawLine(0f, y, scaledWidth.toFloat(), y, paint)
            }
        }

        return scaledBitmap
    }
}
