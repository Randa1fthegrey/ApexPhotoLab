package com.example.apexphotolab.workspace.toolbars.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
    Column(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Filters")
        Button(onClick = { onGreyscaleChange(true) }) {
            Text("Apply Greyscale")
        }
        Button(onClick = { onGreyscaleChange(false) }) {
            Text("Clear Filter")
        }
        Button(onClick = onDismiss) {
            Text("Close")
        }
    }
}
