package com.example.apexphotolab.working_project.tool_panel.save.dialogs

import androidx.compose.material3.*
import androidx.compose.runtime.*

/**
 * Job: UI Specialist (Dialog).
 * Responsibility: Handles the "Rename Snapshot" dialog flow.
 */
@Composable
fun RenameSnapshotDialog(
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Snapshot") },
        text = {
            SnapshotNoteField(
                value = name,
                onValueChange = { name = it },
                description = "Enter a new note for this save point:"
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name.ifBlank { "Manual Save" }) }
            ) {
                Text("Rename")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
