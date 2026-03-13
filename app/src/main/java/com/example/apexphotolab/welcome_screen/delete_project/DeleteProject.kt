package com.example.apexphotolab.welcome_screen.delete_project

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun deleteProject(context: Context, projectDir: DocumentFile): Boolean = withContext(Dispatchers.IO) {
    try {
        if (projectDir.delete()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Project '${projectDir.name}' deleted.", Toast.LENGTH_SHORT).show()
            }
            true
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error deleting project.", Toast.LENGTH_SHORT).show()
            }
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error deleting project.", Toast.LENGTH_SHORT).show()
        }
        false
    }
}

@Composable
fun DeleteConfirmDialog(projectName: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Project?") },
        text = { Text("Are you sure you want to permanently delete '$projectName'? This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
