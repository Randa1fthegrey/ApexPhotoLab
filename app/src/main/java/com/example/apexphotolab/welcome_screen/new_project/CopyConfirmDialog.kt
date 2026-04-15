package com.example.apexphotolab.welcome_screen.new_project

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * Job: UI Component.
 * Dialog for confirming the creation of a new project and copying the image.
 */
@Composable
fun CopyConfirmDialog(projectName: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Project?") },
        text = { Text("This will copy the selected image into a new project named \"$projectName\". The original image will not be affected. Continue?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
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
