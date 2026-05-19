package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import com.example.apexphotolab.the_build.working_project.util.layers.Layer

/**
 * Job: Bitmap Canvas Drawer.
 * Responsibility: Drawing a list of layered bitmaps onto a single new canvas, respecting transform and layer order.
 */
object BitmapDrawer {

    data class LayeredBitmap(
        val bitmap: Bitmap,
        val layer: Layer
    )

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
        }

        return finalBitmap
    }
}
