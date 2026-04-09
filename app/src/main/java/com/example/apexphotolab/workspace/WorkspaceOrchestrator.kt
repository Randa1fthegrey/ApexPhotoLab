package com.example.apexphotolab.workspace

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import com.example.apexphotolab.workspace.tool_panel.layers.Layer
import com.example.apexphotolab.workspace.util.BitmapUtils

/**
 * Job 2: The Data Manager.
 * Orchestrates layer loading, visibility, and Z-order sorting.
 * Now respects EXIF orientation to keep Landscape/Portrait images in their true state.
 */
@Composable
fun WorkspaceOrchestrator(
    layers: List<Layer>,
    colorFilter: ColorFilter?,
    onLayerTransform: (Layer) -> Unit
) {
    val context = LocalContext.current

    // Sort layers by Z-order so they stack correctly on the canvas
    val sortedLayers = remember(layers.toList()) {
        layers.sortedBy { it.zOrder }
    }

    sortedLayers.forEach { layer ->
        if (layer.isVisible) {
            // High-fidelity image loading with EXIF orientation fix
            val bitmap by remember(layer.imageUri) {
                mutableStateOf(BitmapUtils.decodeCorrectedBitmap(context, layer.imageUri))
            }

            bitmap?.let {
                // Hand the loaded layer to the Interaction Expert for manipulation
                SubjectTransformer(
                    layer = layer,
                    bitmap = it,
                    colorFilter = colorFilter,
                    onTransform = onLayerTransform
                )
            }
        }
    }
}
