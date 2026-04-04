package com.example.apexphotolab.workspace.tool_panel.layers.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun RecenterDialog(
    onDismiss: () -> Unit,
    onRecenterAll: () -> Unit,
    onRecenterSpecific: (String) -> Unit
) {
    var layerName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Recenter Canvas",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                // OPTION 1: ALL
                Button(
                    onClick = onRecenterAll,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Recenter ALL Layers")
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // OPTION 2: SPECIFIC
                Text(
                    text = "Recenter Specific Layer",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = layerName,
                    onValueChange = { layerName = it },
                    placeholder = { Text("Layer Name...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onRecenterSpecific(layerName) },
                    enabled = layerName.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Recenter Layer")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    }
}
