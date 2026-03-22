package com.example.apexphotolab.workspace.toolbars.export.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
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
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .statusBarsPadding(), // Ensures content starts below the status bar
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Tools")
                }
                Text(
                    text = "Export Project",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Choose Export Format",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = { onExport(ExportType.PNG) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Export to PNG")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onExport(ExportType.SVG) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Export to SVG")
            }
        }
    }
}
