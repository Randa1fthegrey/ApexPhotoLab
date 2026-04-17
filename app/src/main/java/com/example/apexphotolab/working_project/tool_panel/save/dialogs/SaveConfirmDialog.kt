package com.example.apexphotolab.working_project.tool_panel.save.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

/**
 * Job: UI Specialist (Dialog).
 * Responsibility: Asks the user for save confirmation and tracks the "Do not show again" preference.
 */
@Composable
fun SaveConfirmDialog(
    onDismiss: () -> Unit, 
    onConfirm: (Boolean) -> Unit
) {
    var checked by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Project?") },
        text = {
            Column {
                Text("Save all changes to this project?")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = checked, onCheckedChange = { checked = it })
                    Text("Do not show again")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(checked) }) {
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
