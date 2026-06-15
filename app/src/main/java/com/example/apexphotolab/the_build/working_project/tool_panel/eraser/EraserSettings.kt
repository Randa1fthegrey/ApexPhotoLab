package com.example.apexphotolab.the_build.working_project.tool_panel.eraser

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
            Text(val_util.LABEL_BRUSH_SIZE, style = MaterialTheme.typography.labelSmall)
            Text("${brushSize.toInt()}${val_util.UNIT_PX}", style = MaterialTheme.typography.labelSmall)
        }
        Slider(
            value = brushSize,
            onValueChange = onBrushSizeChange,
            valueRange = val_util.BRUSH_MIN..val_util.BRUSH_MAX,
            modifier = Modifier.height(24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        
        Text(val_util.LABEL_ERASER_MODE, style = MaterialTheme.typography.labelSmall)
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
