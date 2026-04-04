package com.example.apexphotolab.workspace

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.example.apexphotolab.workspace.tool_panel.layers.Layer

/**
 * Job 3: The Interaction Expert.
 * High-Fidelity Update: Live-State Synchronization.
 * Uses rememberUpdatedState to ensure gestures always use the most recent 
 * coordinates, eliminating the "snap-back" jiggle.
 */
@Composable
fun SubjectTransformer(
    layer: Layer,
    bitmap: Bitmap,
    colorFilter: ColorFilter?,
    onTransform: (Layer) -> Unit
) {
    // LIVE WIRE: Ensures the gesture block always sees the latest project data
    val currentLayer by rememberUpdatedState(layer)

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = layer.title,
        colorFilter = colorFilter,
        modifier = Modifier
            .graphicsLayer {
                translationX = layer.xPosition
                translationY = layer.yPosition
                scaleX = layer.scale
                scaleY = layer.scale
                rotationZ = layer.rotation
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
            }
            .pointerInput(layer.id) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    if (currentLayer.isLocked) return@detectTransformGestures

                    // Always calculate deltas based on the freshest state
                    val updatedLayer = currentLayer.copy(
                        xPosition = currentLayer.xPosition + pan.x,
                        yPosition = currentLayer.yPosition + pan.y,
                        scale = (currentLayer.scale * zoom).coerceIn(0.05f, 20f),
                        rotation = currentLayer.rotation + rotation
                    )
                    onTransform(updatedLayer)
                }
            }
    )
}
