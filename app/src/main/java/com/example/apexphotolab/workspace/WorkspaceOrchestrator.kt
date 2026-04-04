package com.example.apexphotolab.workspace

import android.graphics.BitmapFactory
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import com.example.apexphotolab.workspace.tool_panel.layers.Layer

/**
 * Job 2: The Data Manager.
 * Orchestrates layer loading, visibility, and Z-order sorting.
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
            // High-fidelity image loading
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
