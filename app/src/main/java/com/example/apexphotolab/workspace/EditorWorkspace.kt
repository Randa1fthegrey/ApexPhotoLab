package com.example.apexphotolab.workspace

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import com.example.apexphotolab.workspace.tool_panel.layers.Layer

/**
 * The high-level Manager for the drawing area.
 * Updated: Modular architecture. Delegates work to specialized specialists 
 * to ensure buttery smooth performance and zero jiggle.
 */
@Composable
fun EditorWorkspace(
    modifier: Modifier = Modifier,
    layers: List<Layer>,
    colorFilter: ColorFilter?,
    onLayerTransform: (Layer) -> Unit
) {
    // 1. Job 1: GRID SPECIALIST - Handles the background
    WorkspaceBackground(modifier = modifier) {
        // 2. Job 2: DATA MANAGER - Orchestrates the layer stack
        WorkspaceOrchestrator(
            layers = layers,
            colorFilter = colorFilter,
            onLayerTransform = onLayerTransform
        )
    }
}
