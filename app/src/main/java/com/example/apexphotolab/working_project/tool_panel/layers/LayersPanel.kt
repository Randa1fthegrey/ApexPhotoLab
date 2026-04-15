package com.example.apexphotolab.working_project.tool_panel.layers

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.sp

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

@Composable
fun LayerRow(
    layer: Layer, 
    isSelected: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onLayerClick: () -> Unit,
    onVisibilityClick: () -> Unit,
    onLockClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onLayerClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(layer.title, maxLines = 1)
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "🛠️", fontSize = 14.sp)
            }
        }

        IconButton(onClick = onLockClick) {
            Icon(
                imageVector = if (layer.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = "Toggle Lock",
                modifier = Modifier.size(20.dp),
                tint = if (layer.isLocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
        
        IconButton(onClick = onVisibilityClick) {
            Icon(
                if (layer.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle Visibility",
                modifier = Modifier.size(20.dp)
            )
        }

        if (layer.id != "base") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = canMoveUp,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")
                }
                IconButton(
                    onClick = onMoveDown,
                    enabled = canMoveDown,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")
                }
            }
        } else {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.padding(horizontal = 12.dp).size(16.dp),
                tint = MaterialTheme.colorScheme.outline
            )
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
            val nonBaseLayers = layers.filter { it.id != "base" }
            itemsIndexed(nonBaseLayers) { _, layer ->
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
