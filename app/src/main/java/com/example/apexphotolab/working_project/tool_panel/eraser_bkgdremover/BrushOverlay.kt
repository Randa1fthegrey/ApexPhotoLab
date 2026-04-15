package com.example.apexphotolab.working_project.tool_panel.eraser_bkgdremover

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Visual feedback for the Eraser tool.
 * Draws a circle representing the brush size and position.
 */
@Composable
fun BrushOverlay(
    modifier: Modifier = Modifier,
    brushPosition: Offset?,
    brushSize: Float,
    isPrecision: Boolean
) {
    if (brushPosition == null) return

    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw the outer high-contrast ring
        drawCircle(
            color = Color.White,
            radius = brushSize,
            center = brushPosition,
            style = Stroke(
                width = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        )
        
        // Draw a solid inner ring for visibility against light backgrounds
        drawCircle(
            color = Color.Black.copy(alpha = 0.5f),
            radius = brushSize,
            center = brushPosition,
            style = Stroke(width = 1f)
        )

        // If in precision mode, draw a small crosshair in the center
        if (isPrecision) {
            val crosshairSize = 10f
            drawLine(
                color = Color.White,
                start = Offset(brushPosition.x - crosshairSize, brushPosition.y),
                end = Offset(brushPosition.x + crosshairSize, brushPosition.y),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.White,
                start = Offset(brushPosition.x, brushPosition.y - crosshairSize),
                end = Offset(brushPosition.x, brushPosition.y + crosshairSize),
                strokeWidth = 2f
            )
        }
    }
}
