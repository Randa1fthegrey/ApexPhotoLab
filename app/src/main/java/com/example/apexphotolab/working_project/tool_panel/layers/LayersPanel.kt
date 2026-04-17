package com.example.apexphotolab.working_project.tool_panel.layers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.apexphotolab.working_project.util.layers.Layer

/**
 * Job: UI Specialist (Layer Navigator).
 * Responsibility: Manages high-level navigation between the layer list and removal modes.
 */
@Composable
fun LayersPanel(
    modifier: Modifier = Modifier,
    layers: List<Layer>,
    selectedLayerId: String,
    onLayerSelected: (Layer) -> Unit,
    onMoveLayerUp: (Layer) -> Unit,
    onMoveLayerDown: (Layer) -> Unit,
    onAddLayer: () -> Unit,
    onLayerVisibilityChange: (Layer) -> Unit,
    onLayerLockChange: (Layer) -> Unit,
    onLayersRemoved: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var isInRemoveMode by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .statusBarsPadding(), 
        color = MaterialTheme.colorScheme.surface
    ) {
        if (isInRemoveMode) {
            RemoveLayersScreen(
                layers = layers,
                onCancel = { isInRemoveMode = false },
                onConfirm = { ids: Set<String> ->
                    onLayersRemoved(ids)
                    isInRemoveMode = false
                }
            )
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Tools")
                    }
                    Text("Layer Manager", style = MaterialTheme.typography.titleMedium)
                    Row {
                        IconButton(onClick = onAddLayer) {
                            Icon(Icons.Default.Add, contentDescription = "Add Layer")
                        }
                        IconButton(onClick = { isInRemoveMode = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove Layer")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    // We display the list such that the top-most layer (highest index/Z-order) is at the top of the UI
                    itemsIndexed(layers.asReversed()) { reversedIndex, layer ->
                        val originalIndex = (layers.size - 1) - reversedIndex
                        LayerRow(
                            layer = layer,
                            isSelected = layer.id == selectedLayerId,
                            canMoveUp = layer.id != "base" && originalIndex < layers.size - 1,
                            canMoveDown = layer.id != "base" && originalIndex > 1, // Must stay above base at index 0
                            onLayerClick = { onLayerSelected(layer) },
                            onVisibilityClick = { onLayerVisibilityChange(layer) },
                            onLockClick = { onLayerLockChange(layer) },
                            onMoveUp = { onMoveLayerUp(layer) },
                            onMoveDown = { onMoveLayerDown(layer) }
                        )
                    }
                }
            }
        }
    }
}
