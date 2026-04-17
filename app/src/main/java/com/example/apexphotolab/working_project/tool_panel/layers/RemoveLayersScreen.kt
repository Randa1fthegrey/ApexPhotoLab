package com.example.apexphotolab.working_project.tool_panel.layers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.apexphotolab.working_project.util.layers.Layer

/**
 * Job: UI Specialist (Layer Deletion).
 * Responsibility: Provides a selection-based interface for removing multiple layers.
 */
@Composable
fun RemoveLayersScreen(
    layers: List<Layer>,
    onCancel: () -> Unit,
    onConfirm: (Set<String>) -> Unit
) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
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
