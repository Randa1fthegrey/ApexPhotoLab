package com.example.apexphotolab.workspace.tool_panel.save.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SnapshotNameDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val maxChars = 17

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Snapshot Note") },
        text = {
            Column {
                Text("Enter a brief note for this save point:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= maxChars) name = it },
                    label = { Text("Note ($maxChars chars max)") },
                    singleLine = true,
                    placeholder = { Text("e.g., Added blue filter") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name.ifBlank { "Manual Save" }) }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
