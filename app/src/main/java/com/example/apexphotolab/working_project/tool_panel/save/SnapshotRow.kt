package com.example.apexphotolab.working_project.tool_panel.save

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.documentfile.provider.DocumentFile
import java.text.SimpleDateFormat
import java.util.*

/**
 * Job: UI Specialist (History Row).
 * Responsibility: Renders a single history snapshot entry and formats its metadata.
 */
@Composable
fun SnapshotRow(
    snapshot: DocumentFile, 
    isDeleteMode: Boolean,
    isSelected: Boolean,
    onToggleSelection: (Boolean) -> Unit,
    onRollback: () -> Unit,
    onRenameClick: () -> Unit
) {
    val timeFormatter = remember { SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault()) }
    
    val parts = snapshot.name?.removePrefix("save_")?.removeSuffix(".json")?.split("_")
    
    val timePart = if (parts != null && parts.size >= 2) "${parts[0]}_${parts[1]}" else null
    val notePart = if (parts != null && parts.size > 2) parts.drop(2).joinToString(" ") else "Manual Save"
    
    val isProjectBirth = notePart == "Project Birth"

    val date = timePart?.let {
        try {
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).parse(it)
        } catch (e: Exception) { null }
    }

    val displayTime = date?.let { timeFormatter.format(it) } ?: "Unknown Date"
    val fileSize = snapshot.length() / 1024 // KB

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notePart, 
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = displayTime, 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Size: ${fileSize} KB",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            if (isDeleteMode && !isProjectBirth) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onToggleSelection
                )
            } else if (!isDeleteMode) {
                if (!isProjectBirth) {
                    IconButton(onClick = onRenameClick) {
                        Text(text = "✏️", fontSize = 20.sp)
                    }
                }

                IconButton(onClick = onRollback) {
                    Text(text = "🕑", fontSize = 20.sp)
                }
            } else if (isDeleteMode && isProjectBirth) {
                Text("Birth", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}
