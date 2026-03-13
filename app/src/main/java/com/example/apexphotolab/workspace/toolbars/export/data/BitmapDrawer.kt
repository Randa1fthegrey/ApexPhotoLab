package com.example.apexphotolab.workspace.toolbars.export.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

/**
 * A single-responsibility utility for drawing a list of bitmaps onto a new canvas.
 */
object BitmapDrawer {

    /**
     * Draws a list of bitmaps onto a new, single bitmap canvas.
     *
     * @param bitmaps The list of bitmaps to draw. The bitmaps in this list will be recycled.
     * @param width The width of the final canvas.
     * @param height The height of the final canvas.
     * @param applyGreyscale Whether to apply a greyscale filter.
     * @return The final, flattened bitmap.
     */
    fun draw(
        bitmaps: List<Bitmap>,
        width: Int,
        height: Int,
        applyGreyscale: Boolean
    ): Bitmap {
        val finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(finalBitmap)
        val paint = Paint()

        if (applyGreyscale) {
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        }

        bitmaps.forEach { bitmap ->
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
            bitmap.recycle() // Recycle bitmaps as we use them to save memory
        }

        return finalBitmap
    }
}