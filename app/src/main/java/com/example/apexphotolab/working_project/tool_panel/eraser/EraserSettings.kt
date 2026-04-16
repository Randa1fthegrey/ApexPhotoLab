package com.example.apexphotolab.working_project.tool_panel.eraser

import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Job: UI Specialist (Eraser Configuration).
 * Purified: Handles only the visual presentation of eraser-specific parameters.
 */
@Composable
fun EraserSettings(
    brushSize: Float,
    onBrushSizeChange: (Float) -> Unit,
    eraserMode: EraserMode,
    onEraserModeChange: (EraserMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Brush Size", style = MaterialTheme.typography.labelSmall)
            Text("${brushSize.toInt()} px", style = MaterialTheme.typography.labelSmall)
        }
        Slider(
            value = brushSize,
            onValueChange = onBrushSizeChange,
            valueRange = 5f..200f,
            modifier = Modifier.height(24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Eraser Mode", style = MaterialTheme.typography.labelSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EraserMode.entries.forEach { mode ->
                FilterChip(
                    selected = eraserMode == mode,
                    onClick = { onEraserModeChange(mode) },
                    label = { Text(mode.label, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
    }
}
