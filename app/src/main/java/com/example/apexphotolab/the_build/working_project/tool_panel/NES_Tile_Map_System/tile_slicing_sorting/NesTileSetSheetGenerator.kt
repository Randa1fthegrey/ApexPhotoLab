package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Job: Unassembled CHR Tile Sheet Generator.
 * Responsibility: Assembles all unique 8x8 CHR tiles into a side-by-side visual tile sheet,
 * dynamically expanding rows to support up to 960 MMC5 ExRAM tiles.
 */
object NesTileSetSheetGenerator {

    fun generateTileSheet(
        uniqueTiles: List<IntArray>,
        scale: Int = 4,
        drawGridLines: Boolean = true
    ): Bitmap {
        val totalTiles = maxOf(1, uniqueTiles.size)
        val gridCols = 16
        val gridRows = (totalTiles + gridCols - 1) / gridCols // MMC5 can expand up to 60 rows

        val baseWidth = gridCols * 8 // 128 px
        val baseHeight = gridRows * 8

        val rawSheetPixels = IntArray(baseWidth * baseHeight) { Color.BLACK }

        // Render each 8x8 unique tile into the base sheet grid
        uniqueTiles.forEachIndexed { tileIndex, tilePixels ->
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
