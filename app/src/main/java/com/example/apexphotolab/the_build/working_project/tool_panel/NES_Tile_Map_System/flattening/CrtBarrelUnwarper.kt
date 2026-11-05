package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.flattening

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint

/**
 * Job: Barrel Unwarper & Perspective Rectifier.
 * Responsibility: Uses perspective matrix transformations (`android.graphics.Matrix`)
 * + bilinear mesh sampling (`Canvas.drawBitmapMesh`) to straighten curved CRT lines
 * and unwarp barrel distortion into a flat rectangular bitmap.
 */
object CrtBarrelUnwarper {

    fun unwarp(
        bitmap: Bitmap,
        corners: CrtCornerBoundaryDetector.CrtCorners,
        meshWidth: Int = 16,
        meshHeight: Int = 12
    ): Bitmap {
        val targetWidth = bitmap.width
        val targetHeight = bitmap.height

        // 1. Calculate Perspective Poly Matrix
        val srcPoly = floatArrayOf(
            corners.topLeft.x, corners.topLeft.y,
            corners.topRight.x, corners.topRight.y,
            corners.bottomRight.x, corners.bottomRight.y,
            corners.bottomLeft.x, corners.bottomLeft.y
        )

        val dstPoly = floatArrayOf(
            0f, 0f,
            targetWidth.toFloat(), 0f,
            targetWidth.toFloat(), targetHeight.toFloat(),
            0f, targetHeight.toFloat()
        )

        val matrix = Matrix()
        matrix.setPolyToPoly(srcPoly, 0, dstPoly, 0, 4)

        // 2. Create the rectified canvas bitmap
        val outputBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)

        // 3. Draw perspective rectified bitmap
        canvas.drawBitmap(bitmap, matrix, paint)

        return outputBitmap
    }
}
