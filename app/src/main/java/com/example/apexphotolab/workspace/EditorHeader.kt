package com.example.apexphotolab.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * The Project Dashboard header.
 * Displays the Pull-Tab, Metadata, and the "Overdue" Save Timer.
 * Now handles Status Bar padding natively.
 */
@Composable
fun EditorHeader(
    modifier: Modifier = Modifier,
    projectName: String,
    resolution: String,
    activeLayerName: String,
    lastSaveTime: Long, // Epoch timestamp
    activeToolIcon: ImageVector,
    onTabClick: () -> Unit
) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Live update loop for the "Time Since Saved" timer
    LaunchedEffect(lastSaveTime) {
        currentTime = System.currentTimeMillis()
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    val elapsedMillis = if (lastSaveTime == 0L) 0L else currentTime - lastSaveTime
    val seconds = (elapsedMillis / 1000) % 60
    val minutes = (elapsedMillis / 1000) / 60
    
    val timerText = if (lastSaveTime == 0L) "Not Saved" else String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    val isOverdue = minutes >= 5

    Surface(
        modifier = modifier.fillMaxWidth(), // Height is now determined by content + padding
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding() // Pushes content below status bar, but keeps background behind it
                .height(56.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // THE PULL-TAB: Anchored to the top-left
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(64.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { onTabClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activeToolIcon,
                    contentDescription = "Open Tools",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // METADATA COLUMN
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = projectName,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
                Text(
                    text = "$resolution | $activeLayerName",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // WARNING TIMER: Turns red after 5 minutes
            Text(
                text = timerText,
                style = MaterialTheme.typography.titleMedium,
                color = if (isOverdue) Color.Red else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}
