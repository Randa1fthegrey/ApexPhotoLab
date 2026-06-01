package com.example.apexphotolab.the_build.working_project.tool_panel.export.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Job: Format Option Row.
 * Responsibility: Rendering a single selectable format option row with an expandable info toggle.
 */
@Composable
fun FormatOptionRow(
    title: String,
    description: String,
    showInfo: Boolean,
    onInfoToggle: () -> Unit,
    onSelect: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onSelect,
                modifier = Modifier.weight(1f)
            ) {
                Text(title)
            }
            IconButton(onClick = onInfoToggle) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Show Info",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        if (showInfo) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 40.dp)
            )
        }
    }
}
