package com.example.apexphotolab.the_build.working_project.tool_panel.save.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*

/**
 * Job: UI Specialist (Dialog).
 * Responsibility: Handles the "Save Snapshot" dialog flow.
 */
@Composable
fun SnapshotNameDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Snapshot Note") },
        text = {
            SnapshotNoteField(
                value = name,
                onValueChange = { name = it },
                description = "Enter a brief note for this save point:"
            )
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
