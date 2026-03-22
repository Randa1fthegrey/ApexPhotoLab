package com.example.apexphotolab.workspace

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.apexphotolab.workspace.toolbars.layers.Layer

/**
 * The main drawing area for the project.
 * Occupies the full space below the header.
 * Now features a checkerboard background to represent transparency.
 */
@Composable
fun EditorWorkspace(
    modifier: Modifier = Modifier,
    layers: List<Layer>,
    colorFilter: ColorFilter?
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // DRAW CHECKERBOARD BACKGROUND
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
            },
        contentAlignment = Alignment.Center
    ) {
        layers.sortedBy { it.zOrder }.forEach { layer ->
            if (layer.isVisible) {
                val bitmap by remember(layer.imageUri) {
                    mutableStateOf(
                        try {
                            context.contentResolver.openInputStream(layer.imageUri)?.use {
                                BitmapFactory.decodeStream(it)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    )
                }
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = layer.title,
                        colorFilter = colorFilter
                    )
                }
            }
        }
    }
}
