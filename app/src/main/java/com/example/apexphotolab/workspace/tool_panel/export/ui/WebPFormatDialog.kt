package com.example.apexphotolab.workspace.tool_panel.export.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

enum class WebPType { LOSSY, LOSSLESS }

@Composable
fun WebPFormatDialog(
    onDismiss: () -> Unit,
    onConfirm: (WebPType) -> Unit
) {
    var showLossyInfo by remember { mutableStateOf(false) }
    var showLosslessInfo by remember { mutableStateOf(false) }

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
                    text = "WebP Export Format",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                // LOSSY OPTION
                FormatOptionRow(
                    title = "WebP Lossy",
                    description = "Smallest file size. Best for photos and web use.",
                    showInfo = showLossyInfo,
                    onInfoToggle = { showLossyInfo = !showLossyInfo },
                    onSelect = { onConfirm(WebPType.LOSSY) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // LOSSLESS OPTION
                FormatOptionRow(
                    title = "WebP Lossless",
                    description = "Perfect quality. Supports transparency. Larger file size.",
                    showInfo = showLosslessInfo,
                    onInfoToggle = { showLosslessInfo = !showLosslessInfo },
                    onSelect = { onConfirm(WebPType.LOSSLESS) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun FormatOptionRow(
    title: String,
    description: String,
    showInfo: Boolean,
    onInfoToggle: () -> Unit,
    onSelect: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onSelect,
                modifier = Modifier.weight(1f)
            ) {
                Text(title)
            }
            
            IconButton(onClick = onInfoToggle) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Show Info",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        if (showInfo) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 40.dp)
            )
        }
    }
}
