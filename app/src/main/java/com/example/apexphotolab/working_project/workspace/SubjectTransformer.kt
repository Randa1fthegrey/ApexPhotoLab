package com.example.apexphotolab.working_project.workspace

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import com.example.apexphotolab.working_project.tool_panel.layers.Layer

/**
 * Job: Visual Transformer (Passive View).
 * Responsibility: Applying the physical transformations (Scale, Pan, Rotation) 
 * defined in the Layer model to the rendered Bitmap.
 */
@Composable
fun SubjectTransformer(
    layer: Layer,
    bitmap: Bitmap,
    colorFilter: ColorFilter?
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
                transformOrigin = TransformOrigin.Center
            }
    )
}
