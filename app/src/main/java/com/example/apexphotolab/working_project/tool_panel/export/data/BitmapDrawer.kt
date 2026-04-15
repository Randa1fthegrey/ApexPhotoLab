package com.example.apexphotolab.working_project.tool_panel.export.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import com.example.apexphotolab.working_project.tool_panel.layers.Layer

/**
 * A single-responsibility utility for drawing a list of bitmaps onto a new canvas.
 * Updated: Supports full transformation (Scale, Rotation, Position) and respects layer order.
 */
object BitmapDrawer {

    data class LayeredBitmap(
        val bitmap: Bitmap,
        val layer: Layer
    )

    /**
     * Draws a list of bitmaps onto a new, single bitmap canvas using their layer metadata.
     *
     * @param items The list of LayeredBitmap items. The bitmaps in this list will be recycled.
     * @param width The width of the final canvas.
     * @param height The height of the final canvas.
     * @param applyGreyscale Whether to apply a greyscale filter.
     * @return The final, flattened bitmap.
     */
    fun draw(
        items: List<LayeredBitmap>,
        width: Int,
        height: Int,
        applyGreyscale: Boolean
    ): Bitmap {
        val finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(finalBitmap)
        val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)

        if (applyGreyscale) {
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        }

        items.forEach { item ->
            val matrix = Matrix()
            val layer = item.layer
            val bitmap = item.bitmap

            // 1. Scale
            matrix.postScale(layer.scale, layer.scale)
            
            // 2. Rotate (around center of scaled bitmap)
            val centerX = (bitmap.width * layer.scale) / 2f
            val centerY = (bitmap.height * layer.scale) / 2f
            matrix.postRotate(layer.rotation, centerX, centerY)
            
            // 3. Translate (Position)
            matrix.postTranslate(layer.xPosition, layer.yPosition)

            canvas.drawBitmap(bitmap, matrix, paint)
            
            // Cleanup to save memory during high-res exports
            bitmap.recycle()
        }

        return finalBitmap
    }
}
