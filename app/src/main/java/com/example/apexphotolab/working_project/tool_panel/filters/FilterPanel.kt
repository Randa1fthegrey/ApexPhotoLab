package com.example.apexphotolab.working_project.tool_panel.filters

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterPanel(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onGreyscaleChange: (Boolean) -> Unit
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
                    text = "Image Filters",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onGreyscaleChange(true) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Apply Greyscale")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { onGreyscaleChange(false) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Clear Filters")
            }
        }
    }
}
