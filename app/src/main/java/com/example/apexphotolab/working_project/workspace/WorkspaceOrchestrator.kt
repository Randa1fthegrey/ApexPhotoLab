package com.example.apexphotolab.working_project.workspace

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import com.example.apexphotolab.working_project.util.layers.Layer
import com.example.apexphotolab.working_project.util.bitmap.BitmapLoader
import com.example.apexphotolab.working_project.tool_panel.util.BitmapRegistry

/**
 * Job: Data Orchestrator (Rendering Engine).
 * Responsibility: Managing the layer stack and Z-order sorting.
 * Purified: Focuses strictly on the assembly of the visual stack.
 */
@Composable
fun WorkspaceOrchestrator(
    layers: List<Layer>,
    colorFilter: ColorFilter?
) {
    val context = LocalContext.current

    // 1. Logic: Maintain Z-order sorting
    val sortedLayers = remember(layers) {
        layers.sortedBy { it.zOrder }
    }

    // 2. Orchestration: Iterate and delegate rendering
    sortedLayers.forEach { layer ->
        if (layer.isVisible) {
            val bitmap = resolveLayerBitmap(context, layer)
            
            bitmap?.let {
                SubjectTransformer(
                    layer = layer,
                    bitmap = it,
                    colorFilter = colorFilter
                )
            }
        }
    }
}

/**
 * Specialized Helper: Bitmap Retrieval Strategy.
 * Prioritizes live edits from the Registry, falls back to disk.
 */
@Composable
private fun resolveLayerBitmap(context: android.content.Context, layer: Layer): android.graphics.Bitmap? {
    // We include the revision as a key to force recomposition when the tool modifies pixels.
    return remember(layer.id, layer.imageUri, BitmapRegistry.getRevision(layer.id)) {
        BitmapRegistry.getWorkingBitmap(layer.id) 
            ?: BitmapLoader.decodeCorrectedBitmap(context, layer.imageUri)
    }
}
