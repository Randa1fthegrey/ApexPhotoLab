package com.example.apexphotolab.the_build.working_project.tool_panel.layers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apexphotolab.the_build.working_project.util.layers.Layer

/**
 * Job: UI Specialist (Layer Row).
 * Responsibility: Displays a single layer's information and provides individual controls.
 */
@Composable
fun LayerRow(
    layer: Layer,
    isSelected: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onLayerClick: () -> Unit,
    onVisibilityClick: () -> Unit,
    onLockClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onLayerClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(layer.title, maxLines = 1)
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "🛠️", fontSize = 14.sp)
            }
        }

        IconButton(onClick = onLockClick) {
            Icon(
                imageVector = if (layer.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = "Toggle Lock",
                modifier = Modifier.size(20.dp),
                tint = if (layer.isLocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
        
        IconButton(onClick = onVisibilityClick) {
            Icon(
                if (layer.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle Visibility",
                modifier = Modifier.size(20.dp)
            )
        }

        if (layer.id != "base") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = canMoveUp,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")
                }
                IconButton(
                    onClick = onMoveDown,
                    enabled = canMoveDown,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")
                }
            }
        } else {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.padding(horizontal = 12.dp).size(16.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

