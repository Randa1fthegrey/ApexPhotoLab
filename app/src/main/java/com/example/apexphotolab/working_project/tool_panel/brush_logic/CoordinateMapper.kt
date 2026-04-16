package com.example.apexphotolab.working_project.tool_panel.brush_logic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.apexphotolab.working_project.tool_panel.layers.Layer
import kotlin.math.cos
import kotlin.math.sin

/**
 * The Math Engine for the Workspace.
 * Maps screen-space touch coordinates to bitmap-space pixel coordinates.
 */
object CoordinateMapper {

    /**
     * Maps a touch offset (relative to the workspace container) to a pixel coordinate
     * on the provided layer's bitmap.
     * @param fitScale The ratio of "Screen Pixels" to "Project Pixels" for the project canvas.
     */
    fun mapToBitmap(
        touchOffset: Offset,
        containerSize: IntSize,
        layer: Layer,
        bitmapWidth: Int,
        bitmapHeight: Int,
        fitScale: Float
    ): Offset {
        // 1. Move the origin to the center of the container
        val centerX = containerSize.width / 2f
        val centerY = containerSize.height / 2f

        var x = touchOffset.x - centerX
        var y = touchOffset.y - centerY

        // 2. Account for Layer Translation (in screen pixels)
        x -= layer.xPosition
        y -= layer.yPosition

        // 3. Account for Layer Rotation (Inverse)
        val angleRad = Math.toRadians(-layer.rotation.toDouble())
        val rotatedX = x * cos(angleRad) - y * sin(angleRad)
        val rotatedY = x * sin(angleRad) + y * cos(angleRad)

        // 4. Account for Layer Scale AND Fit Scale (Inverse)
        // Total Scale = (Native to Canvas) * (Canvas to Interaction)
        val totalScale = layer.scale * fitScale
        var finalX = rotatedX.toFloat() / totalScale
        var finalY = rotatedY.toFloat() / totalScale

        // 5. Map from Layer Center to Bitmap Top-Left
        finalX += bitmapWidth / 2f
        finalY += bitmapHeight / 2f

        return Offset(finalX, finalY)
    }
}