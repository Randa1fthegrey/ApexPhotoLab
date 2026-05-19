package com.example.apexphotolab.the_build.working_project.tool_panel.save.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Job: UI Specialist (Row).
 * Responsibility: A standardized input field for snapshot notes with character limits.
 */
@Composable
fun SnapshotNoteField(
    value: String,
    onValueChange: (String) -> Unit,
    description: String,
    modifier: Modifier = Modifier
) {
    val maxChars = 17
    Column(modifier = modifier) {
        Text(description)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { if (it.length <= maxChars) onValueChange(it) },
            label = { Text("Note ($maxChars chars max)") },
            singleLine = true,
            placeholder = { Text("e.g., Added blue filter") }
        )
    }
}
