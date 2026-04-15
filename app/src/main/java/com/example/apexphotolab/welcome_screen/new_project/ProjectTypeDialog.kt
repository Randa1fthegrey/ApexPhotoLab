package com.example.apexphotolab.welcome_screen.new_project

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Job: UI Component.
 * Dialog for selecting the type of project to create.
 */
@Composable
fun ProjectTypeDialog(
    onDismiss: () -> Unit,
    onConfirm: (ProjectType) -> Unit
) {
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
                    text = "Select Project Type",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                // STATIC OPTION (Single Image)
                Button(
                    onClick = { onConfirm(ProjectType.STATIC) },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Static / PNG (Single)")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ANIMATED OPTION (Sequence -> Time)
                OutlinedButton(
                    onClick = { /* Future logic: onConfirm(ProjectType.ANIMATED) */ },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Animated / GIF (Motion)")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SEQUENTIAL OPTION (Sequence -> Pages)
                OutlinedButton(
                    onClick = { /* Future logic: onConfirm(ProjectType.SEQUENTIAL) */ },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Sequential / PDF (Document)")
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    }
}
