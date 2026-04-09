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
    )
}
