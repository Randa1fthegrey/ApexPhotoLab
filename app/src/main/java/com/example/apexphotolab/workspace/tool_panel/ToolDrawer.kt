package com.example.apexphotolab.workspace.tool_panel

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

/**
 * The slide-out tool panel.
 * Updated: Renamed Recenter to Reset Canvas.
 */
@Composable
fun ToolDrawer(
    onToolClick: (ImageVector, () -> Unit) -> Unit,
    onFilterClick: () -> Unit,
    onAddImageClick: () -> Unit,
    onSaveClick: () -> Unit,
    onLayersClick: () -> Unit,
    onExportClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onResetClick: () -> Unit, // Renamed callback
    onHomeClick: () -> Unit,
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
            DrawerItem(Icons.Default.Save, "Save Project") {
                onToolClick(Icons.Default.Save, onSaveClick)
            }
            DrawerItem(Icons.Default.AccessTime, "Rollback History") {
                onToolClick(Icons.Default.AccessTime, onHistoryClick)
            }
            DrawerItem(Icons.Default.FileUpload, "Export to File") {
                onToolClick(Icons.Default.FileUpload, onExportClick)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // RENAMED TOOL
            DrawerItem(Icons.Default.CenterFocusStrong, "Reset Canvas") {
                onToolClick(Icons.Default.CenterFocusStrong, onResetClick)
            }
            DrawerItem(Icons.Default.Tonality, "Image Filters") {
                onToolClick(Icons.Default.Tonality, onFilterClick)
            }
            DrawerItem(Icons.Default.Layers, "Layer Manager") {
                onToolClick(Icons.Default.Layers, onLayersClick)
            }
            DrawerItem(Icons.Default.Add, "Add New Layer") {
                onToolClick(Icons.Default.Add, onAddImageClick)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            DrawerItem(Icons.Default.Search, "Search Assets") { onToolClick(Icons.Default.Search) {} }
            DrawerItem(Icons.Default.Settings, "App Settings") { onToolClick(Icons.Default.Settings) {} }
            DrawerItem(Icons.Default.Info, "Project Info") { onToolClick(Icons.Default.Info) {} }
            DrawerItem(Icons.Default.Favorite, "Favorites") { onToolClick(Icons.Default.Favorite) {} }
            DrawerItem(Icons.AutoMirrored.Filled.Send, "Share Project") { onToolClick(Icons.AutoMirrored.Filled.Send) {} }
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
