package com.example.apexphotolab.working_project.tool_panel.eraser

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.ui.geometry.Offset

/**
 * Job: Execution Worker (Destructive Mutation).
 * Responsibility: Performs the physical "erasing" of pixels on a bitmap using
 * PorterDuff.Mode.CLEAR to manipulate the alpha channel.
 *
 * Purified: Identity-locked to the Eraser tool. Does not handle coordinate 
 * mapping or input detection—only the execution of the mutation.
 */
object EraserBrush {

    private val erasePaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    /**
     * Erases a point on the bitmap. 
     * @param bitmap The mutable bitmap to modify.
     * @param point The local bitmap coordinate (calculated by CoordinateMapper).
     * @param radius The size of the eraser brush.
     */
    fun erase(bitmap: Bitmap, point: Offset, radius: Float) {
        val canvas = Canvas(bitmap)
        erasePaint.strokeWidth = radius * 2
        
        // We use a tiny path to ensure the stroke cap (round) is drawn correctly at a single point
        val path = Path()
        path.moveTo(point.x, point.y)
        path.lineTo(point.x + 0.1f, point.y + 0.1f)
        
        canvas.drawPath(path, erasePaint)
    }

    /**
     * Erases a continuous line (stroke) between two points.
     */
    fun eraseStroke(bitmap: Bitmap, from: Offset, to: Offset, radius: Float) {
        val canvas = Canvas(bitmap)
        erasePaint.strokeWidth = radius * 2
        
        canvas.drawLine(from.x, from.y, to.x, to.y, erasePaint)
    }
}
