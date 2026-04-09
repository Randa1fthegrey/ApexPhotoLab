package com.example.apexphotolab.workspace

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.apexphotolab.workspace.tool_panel.layers.Layer

/**
 * The high-level Manager for the drawing area.
 * Updated: Global gesture handling with "Live-State" synchronization.
 * The entire workspace acts as a controller for the selected layer.
 * Now introduces a "Project Canvas Anchor" that respects the base image aspect ratio
 * while maintaining an infinite checkerboard background.
 */
@Composable
fun EditorWorkspace(
    modifier: Modifier = Modifier,
    layers: List<Layer>,
    selectedLayerId: String,
    colorFilter: ColorFilter?,
    onLayerTransform: (Layer) -> Unit
) {
    val currentOnTransform by rememberUpdatedState(onLayerTransform)
    val currentLayers by rememberUpdatedState(layers)
    val currentSelectedId by rememberUpdatedState(selectedLayerId)

    // Calculate aspect ratio from base layer if available
    val baseLayer = layers.find { it.id == "base" }
    val canvasAspectRatio = if (baseLayer != null && baseLayer.width > 0 && baseLayer.height > 0) {
        baseLayer.width.toFloat() / baseLayer.height.toFloat()
    } else {
        1f // Default to square if not yet determined
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(selectedLayerId) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    val layer = currentLayers.find { it.id == currentSelectedId }
                    layer?.let { 
                        if (it.isLocked) return@detectTransformGestures
                        val updatedLayer = it.copy(
                            xPosition = it.xPosition + pan.x,
                            yPosition = it.yPosition + pan.y,
                            scale = (it.scale * zoom).coerceIn(0.05f, 20f),
                            rotation = it.rotation + rotation
                        )
                        currentOnTransform(updatedLayer)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // 1. INFINITE GRID - Always fills the entire workstation area
        WorkspaceBackground(modifier = Modifier.fillMaxSize()) {
            // Empty content, just used for the background drawing
        }

        // 2. PROJECT CANVAS ANCHOR
        // This transparent box defines the project's "True North" boundaries.
        // It provides the coordinate center and aspect ratio for all layers.
        Box(
            modifier = Modifier
                .padding(32.dp) // Leave a bit of gutter so user sees the edges
                .aspectRatio(canvasAspectRatio, matchHeightConstraintsFirst = true),
            contentAlignment = Alignment.Center
        ) {
            WorkspaceOrchestrator(
                layers = layers,
                colorFilter = colorFilter,
                onLayerTransform = onLayerTransform
            )
        }
    }
}
