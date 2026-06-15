package com.example.apexphotolab.the_build.welcome_screen.new_project.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.apexphotolab.the_build.welcome_screen.new_project.ProjectType

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
                    text = val_util.TYPE_TITLE,
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                // STATIC OPTION (Single Image)
                Button(
                    onClick = { onConfirm(ProjectType.STATIC) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                ) {
                    Text(val_util.TYPE_STATIC)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ANIMATED OPTION (Sequence -> Time)
                OutlinedButton(
                    onClick = { /* Future logic: onConfirm(ProjectType.ANIMATED) */ },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                ) {
                    Text(val_util.TYPE_ANIMATED)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SEQUENTIAL OPTION (Sequence -> Pages)
                OutlinedButton(
                    onClick = { /* Future logic: onConfirm(ProjectType.SEQUENTIAL) */ },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                ) {
                    Text(val_util.TYPE_SEQUENTIAL)
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onDismiss) {
                    Text(val_util.BUTTON_CANCEL)
                }
            }
        }
    }
}
