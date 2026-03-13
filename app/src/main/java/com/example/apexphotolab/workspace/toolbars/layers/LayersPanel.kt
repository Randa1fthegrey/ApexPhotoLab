package com.example.apexphotolab.workspace.toolbars.layers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LayersPanel(
    modifier: Modifier = Modifier,
    layers: List<Layer>,
    onAddLayer: () -> Unit,
    onLayerVisibilityChange: (Layer) -> Unit,
    onLayersRemoved: (Set<String>) -> Unit
) {
    var isInRemoveMode by remember { mutableStateOf(false) }

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
        Column(
            modifier = modifier
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Layers")
                Row {
                    IconButton(onClick = onAddLayer) {
                        Icon(Icons.Default.Add, contentDescription = "Add Layer")
                    }
                    IconButton(onClick = { isInRemoveMode = true }) {
                        Icon(Icons.Default.Remove, contentDescription = "Remove Layer")
                    }
                }
            }

            LazyColumn {
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

@Composable
fun LayerRow(
    layer: Layer,
    onVisibilityClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(layer.title, modifier = Modifier.weight(1f))
        IconButton(onClick = onVisibilityClick) {
            Icon(
                if (layer.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle Layer Visibility"
            )
        }
        if (layer.id != "base") {
            IconButton(onClick = { /* TODO: Implement Reordering */ }) {
                Icon(Icons.Default.DragHandle, contentDescription = "Reorder Layer")
            }
        }
    }
}

@Composable
fun RemoveLayersScreen(
    layers: List<Layer>,
    onCancel: () -> Unit,
    onConfirm: (Set<String>) -> Unit
) {
    val selectedLayerIds = remember { mutableStateOf(setOf<String>()) }

    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
            Text("Remove Layers")
            Button(onClick = { onConfirm(selectedLayerIds.value) }) {
                Text("Confirm")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(layers.filter { it.id != "base" }) { layer ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectedLayerIds.value.contains(layer.id),
                        onCheckedChange = { isChecked ->
                            val currentIds = selectedLayerIds.value.toMutableSet()
                            if (isChecked) {
                                currentIds.add(layer.id)
                            } else {
                                currentIds.remove(layer.id)
                            }
                            selectedLayerIds.value = currentIds
                        }
                    )
                    Text(layer.title)
                }
            }
        }
    }
}
