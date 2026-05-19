package com.example.apexphotolab.the_build.working_project.header

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Job: Orchestrator.
 * The Project Dashboard header.
 * Assembles the Pull-Tab, Metadata Column, and Save Timer.
 */
@Composable
fun EditorHeader(
    modifier: Modifier = Modifier,
    projectName: String,
    resolution: String,
    currentResolution: String,
    activeLayerName: String,
    isLayerLocked: Boolean,
    lastSaveTime: Long,
    activeToolIcon: ImageVector,
    onTabClick: () -> Unit,
    onLockToggle: () -> Unit,
    onQuickSaveClick: () -> Unit
) {
    val timerState = rememberSaveTimerState(lastSaveTime)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Column {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Row(
                modifier = Modifier
                    .defaultMinSize(minHeight = 56.dp)
                    .height(IntrinsicSize.Min)
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

                // METADATA COLUMN (Worker)
                ProjectMetadataColumn(
                    modifier = Modifier.weight(1f),
                    projectName = projectName,
                    nativeResolution = resolution,
                    currentResolution = currentResolution,
                    activeLayerName = activeLayerName,
                    isLayerLocked = isLayerLocked,
                    onLockToggle = onLockToggle
                )

                // QUICK SAVE AND TIMER: 💾 = 00:00
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    IconButton(
                        onClick = onQuickSaveClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text(text = "💾", fontSize = 18.sp)
                    }
                    Text(
                        text = "=",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = timerState.text,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (timerState.isOverdue) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
