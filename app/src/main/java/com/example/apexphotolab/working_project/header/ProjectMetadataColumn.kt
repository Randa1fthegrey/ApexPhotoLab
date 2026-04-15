package com.example.apexphotolab.working_project.header

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Job: UI Component.
 * Displays project metadata and layer lock status.
 */
@Composable
fun ProjectMetadataColumn(
    modifier: Modifier = Modifier,
    projectName: String,
    nativeResolution: String,
    currentResolution: String,
    activeLayerName: String,
    isLayerLocked: Boolean,
    onLockToggle: () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = projectName,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1
        )
        Text(
            text = "Native: $nativeResolution",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            maxLines = 1
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Current: $currentResolution | $activeLayerName",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onLockToggle,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isLayerLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = "Toggle Lock",
                    tint = if (isLayerLocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
