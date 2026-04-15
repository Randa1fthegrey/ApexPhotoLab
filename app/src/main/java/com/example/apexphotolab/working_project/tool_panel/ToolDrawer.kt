package com.example.apexphotolab.working_project.tool_panel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.apexphotolab.working_project.WorkspaceTool
import com.example.apexphotolab.working_project.icon
import com.example.apexphotolab.working_project.label
import com.example.apexphotolab.working_project.tool_panel.eraser_bkgdremover.EraserMode
import com.example.apexphotolab.working_project.tool_panel.eraser_bkgdremover.label

/**
 * The slide-out tool panel.
 * Updated: Added Eraser Mode selection.
 */
@Composable
fun ToolDrawer(
    activeTool: WorkspaceTool,
    onToolSelected: (WorkspaceTool) -> Unit,
    onFilterClick: () -> Unit,
    onAddImageClick: () -> Unit,
    onSaveClick: () -> Unit,
    onLayersClick: () -> Unit,
    onExportClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onResetClick: () -> Unit,
    onHomeClick: () -> Unit,
    brushSize: Float,
    onBrushSizeChange: (Float) -> Unit,
    eraserMode: EraserMode,
    onEraserModeChange: (EraserMode) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Workshop Tools",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Close Drawer"
                    )
                }
            }

            DrawerItem(Icons.Default.Home, "Return Home", onHomeClick)
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            Text(text = "Active Tool", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            
            WorkspaceTool.entries.forEach { tool ->
                NavigationDrawerItem(
                    icon = { Icon(tool.icon, contentDescription = null) },
                    label = { Text(tool.label) },
                    selected = activeTool == tool,
                    onClick = { onToolSelected(tool) },
                    modifier = Modifier.height(48.dp)
                )
            }

            if (activeTool == WorkspaceTool.ERASER) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
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

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            DrawerItem(Icons.Default.Save, "Save Project", onSaveClick)
            DrawerItem(Icons.Default.AccessTime, "Rollback History", onHistoryClick)
            DrawerItem(Icons.Default.FileUpload, "Export to File", onExportClick)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            DrawerItem(Icons.Default.CenterFocusStrong, "Reset Canvas", onResetClick)
            DrawerItem(Icons.Default.Tonality, "Image Filters", onFilterClick)
            DrawerItem(Icons.Default.Layers, "Layer Manager", onLayersClick)
            DrawerItem(Icons.Default.Add, "Add New Layer", onAddImageClick)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            DrawerItem(Icons.Default.Search, "Search Assets") { }
            DrawerItem(Icons.Default.Settings, "App Settings") { }
            DrawerItem(Icons.Default.Info, "Project Info") { }
            DrawerItem(Icons.Default.Favorite, "Favorites") { }
            DrawerItem(Icons.AutoMirrored.Filled.Send, "Share Project") { }
        }
    }
}

@Composable
private fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.height(48.dp)
    )
}
