package com.example.apexphotolab.workspace.toolbars.export.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.apexphotolab.workspace.toolbars.export.data.ExportType

@Composable
fun ExportScreen(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onExport: (ExportType) -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Choose Export Format")
        Button(onClick = { onExport(ExportType.PNG) }) {
            Text("Export to PNG")
        }
        Button(onClick = { onExport(ExportType.SVG) }) {
            Text("Export to SVG")
        }
        Button(onClick = onDismiss) {
            Text("Cancel")
        }
    }
}
