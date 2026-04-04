package com.example.apexphotolab.workspace

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Job 1: The Grid Specialist.
 * Responsible solely for rendering the checkerboard transparency background.
 */
@Composable
fun WorkspaceBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val squareSize = 16.dp.toPx()
                val columns = (size.width / squareSize).toInt() + 1
                val rows = (size.height / squareSize).toInt() + 1

                for (i in 0 until columns) {
                    for (j in 0 until rows) {
                        val color = if ((i + j) % 2 == 0) Color(0xFFE0E0E0) else Color(0xFFD0D0D0)
                        drawRect(
                            color = color,
                            topLeft = Offset(i * squareSize, j * squareSize),
                            size = Size(squareSize, squareSize)
                        )
                    }
                }
            }
    ) {
        content()
    }
}
