package com.example.apexphotolab.the_build.working_project.tool_panel.filters

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Job: UI Specialist (Filter Rack).
 * Responsibility: Displays a list of available filters and handles selection events.
 * 
 * Purified: Filter-Blind. It dynamically generates buttons based on the 
 * provided list of WorkspaceFilter objects.
 */
@Composable
fun FilterPanel(
    availableFilters: List<WorkspaceFilter>,
    onFilterSelected: (WorkspaceFilter?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .statusBarsPadding(),
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

            // Dynamically generate buttons for each available filter
            availableFilters.forEach { filter ->
                Button(
                    onClick = { onFilterSelected(filter) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                ) {
                    Text(filter.label)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // The "Clear" option is always present
            OutlinedButton(
                onClick = { onFilterSelected(null) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
            ) {
                Text("Clear Filters")
            }
        }
    }
}
