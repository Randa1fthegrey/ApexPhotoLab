package com.example.apexphotolab.the_build.welcome_screen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile

/**
 * Job: UI Component.
 * Renders a single project row in the explorer.
 */
@Composable
fun ProjectItemRow(
    file: DocumentFile,
    isDeleteMode: Boolean,
    onProjectSelected: (DocumentFile) -> Unit,
    onDeleteRequest: (DocumentFile) -> Unit
) {
    Text(
        text = file.name ?: "Unknown Project",
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                if (isDeleteMode) onDeleteRequest(file) else onProjectSelected(file)
            }
            .padding(16.dp)
    )
}
