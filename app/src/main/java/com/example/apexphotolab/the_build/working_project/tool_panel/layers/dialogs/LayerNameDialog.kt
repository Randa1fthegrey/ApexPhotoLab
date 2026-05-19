package com.example.apexphotolab.the_build.working_project.tool_panel.layers.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Job: UI Specialist (Dialog).
 * Responsibility: Provides a transient UI for naming a new layer with length constraints.
 */
@Composable
fun LayerNameDialog(
    onDismiss: () -> Unit, 
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("New Layer") }
    val maxChars = 20

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Name Your Layer") },
        text = {
            Column {
                Text("Give this layer a recognizable name:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { if (it.length <= maxChars) text = it },
                    label = { Text("Layer Name ($maxChars chars max)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text.ifBlank { "New Layer" }) }
            ) {
                Text("Continue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
