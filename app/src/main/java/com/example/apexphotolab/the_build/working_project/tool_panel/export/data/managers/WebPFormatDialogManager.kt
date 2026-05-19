package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.managers

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.apexphotolab.the_build.working_project.tool_panel.export.ui.FormatOptionRow
import com.example.apexphotolab.the_build.working_project.tool_panel.export.ui.WebPType

/**
 * Job: WebP Format Dialog Orchestrator.
 * Responsibility: Assembling the WebP format selection dialog from its component parts.
 */
@Composable
fun WebPFormatDialog(
    onDismiss: () -> Unit,
    onConfirm: (WebPType) -> Unit
) {
    var showLossyInfo by remember { mutableStateOf(false) }
    var showLosslessInfo by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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

                FormatOptionRow(
                    title = "WebP Lossy",
                    description = "Smallest file size. Best for photos and web use.",
                    showInfo = showLossyInfo,
                    onInfoToggle = { showLossyInfo = !showLossyInfo },
                    onSelect = { onConfirm(WebPType.LOSSY) }
                )

                Spacer(modifier = Modifier.height(16.dp))

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
