package com.example.apexphotolab.the_build.welcome_screen.new_project.ui

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
        title = { Text(val_util.COPY_TITLE) },
        text = { Text("${val_util.COPY_MESSAGE_START}$projectName${val_util.COPY_MESSAGE_END}") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(val_util.BUTTON_CONTINUE)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(val_util.BUTTON_CANCEL)
            }
        }
    )
}
