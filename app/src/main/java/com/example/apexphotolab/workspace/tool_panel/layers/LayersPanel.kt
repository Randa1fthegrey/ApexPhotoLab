package com.example.apexphotolab.workspace.tool_panel.layers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LayersPanel(
    modifier: Modifier = Modifier,
    layers: List<Layer>,
    onAddLayer: () -> Unit,
    onLayerVisibilityChange: (Layer) -> Unit,
    onLayersRemoved: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var isInRemoveMode by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .statusBarsPadding(), // Ensures content starts below the status bar
        color = MaterialTheme.colorScheme.surface
    ) {
        if (isInRemoveMode) {
            RemoveLayersScreen(
                layers = layers,
                onCancel = { isInRemoveMode = false },
                onConfirm = { ids ->
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
                    items(layers) { layer ->
                        LayerRow(
                            layer = layer,
                            onVisibilityClick = { onLayerVisibilityChange(layer) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LayerRow(layer: Layer, onVisibilityClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(layer.title, modifier = Modifier.weight(1f))
        IconButton(onClick = onVisibilityClick) {
            Icon(
                if (layer.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle Visibility"
            )
        }
        if (layer.id != "base") {
            IconButton(onClick = { /* Reorder placeholder */ }) {
                Icon(Icons.Default.DragHandle, contentDescription = "Reorder")
            }
        }
    }
}

@Composable
fun RemoveLayersScreen(layers: List<Layer>, onCancel: () -> Unit, onConfirm: (Set<String>) -> Unit) {
    val selectedLayerIds = remember { mutableStateOf(setOf<String>()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) { Text("Cancel") }
            Text("Delete Layers", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { onConfirm(selectedLayerIds.value) }) { Text("Confirm") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(layers.filter { it.id != "base" }) { layer ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Checkbox(
                        checked = selectedLayerIds.value.contains(layer.id),
                        onCheckedChange = { isChecked ->
                            val currentIds = selectedLayerIds.value.toMutableSet()
                            if (isChecked) currentIds.add(layer.id) else currentIds.remove(layer.id)
                            selectedLayerIds.value = currentIds
                        }
                    )
                    Text(layer.title)
                }
            }
        }
    }
}
