package com.example.apexphotolab.workspace.tool_panel.export.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.apexphotolab.workspace.tool_panel.export.data.ExportType

enum class ResolutionCategory {
    WIDESCREEN, STANDARD, CUSTOM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    modifier: Modifier = Modifier,
    category: ResolutionCategory,
    widescreenIndex: Int,
    standardIndex: Int,
    customW: String,
    customH: String,
    onCategoryChange: (ResolutionCategory) -> Unit,
    onWidescreenIndexChange: (Int) -> Unit,
    onStandardIndexChange: (Int) -> Unit,
    onCustomWidthChange: (String) -> Unit,
    onCustomHeightChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onExport: (ExportType) -> Unit,
    onResolutionChange: (String) -> Unit
) {
    val widescreenPresets = listOf(
        "1280 x 720" to "Standard (16:9)",
        "1920 x 1080" to "Full HD (16:9)",
        "3840 x 2160" to "4K Ultra (16:9)"
    )

    val standardPresets = listOf(
        "1024 x 1024" to "Standard (1:1)",
        "2048 x 2048" to "HD (1:1)",
        "4096 x 4096" to "4K Ultra (1:1)"
    )

    var showWebpDialog by remember { mutableStateOf(false) }

    LaunchedEffect(category, widescreenIndex, standardIndex, customW, customH) {
        val resolution = when (category) {
            ResolutionCategory.WIDESCREEN -> widescreenPresets[widescreenIndex].first
            ResolutionCategory.STANDARD -> standardPresets[standardIndex].first
            ResolutionCategory.CUSTOM -> "$customW x $customH"
        }
        onResolutionChange(resolution)
    }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .safeDrawingPadding(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Export Settings",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- RESOLUTION SECTION ---
            ExportSectionTitle("Resolution")
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = category == ResolutionCategory.WIDESCREEN,
                    onClick = { onCategoryChange(ResolutionCategory.WIDESCREEN) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) { Text("Wide") }
                SegmentedButton(
                    selected = category == ResolutionCategory.STANDARD,
                    onClick = { onCategoryChange(ResolutionCategory.STANDARD) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                ) { Text("Square") }
                SegmentedButton(
                    selected = category == ResolutionCategory.CUSTOM,
                    onClick = { onCategoryChange(ResolutionCategory.CUSTOM) },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                ) { Text("Custom") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth().height(160.dp).selectableGroup()) {
                when (category) {
                    ResolutionCategory.WIDESCREEN -> {
                        widescreenPresets.forEachIndexed { index, pair ->
                            ResolutionOption(
                                text = "${pair.first} = ${pair.second}", 
                                selected = (index == widescreenIndex), 
                                onClick = { onWidescreenIndexChange(index) }
                            )
                        }
                    }
                    ResolutionCategory.STANDARD -> {
                        standardPresets.forEachIndexed { index, pair ->
                            ResolutionOption(
                                text = "${pair.first} = ${pair.second}", 
                                selected = (index == standardIndex), 
                                onClick = { onStandardIndexChange(index) }
                            )
                        }
                    }
                    ResolutionCategory.CUSTOM -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = customW, onValueChange = { onCustomWidthChange(it.filter { c -> c.isDigit() }) }, label = { Text("Width") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                            OutlinedTextField(value = customH, onValueChange = { onCustomHeightChange(it.filter { c -> c.isDigit() }) }, label = { Text("Height") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // --- FILE TYPE SECTION ---
            ExportSectionTitle("Format")
            
            Text("Web & Mobile", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            ExportButton(text = "PNG (High Quality)", enabled = true, onClick = { onExport(ExportType.PNG) })
            ExportButton(text = "JPG (Optimized)", enabled = true, onClick = { onExport(ExportType.JPG) })
            ExportButton(text = "WebP (Ultra Compact)", enabled = true, onClick = { showWebpDialog = true })
            ExportButton(text = "SVG (Vector Shape)", enabled = true, onClick = { onExport(ExportType.SVG) })

            Spacer(modifier = Modifier.height(16.dp))

            Text("Professional & Print", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            ExportButton(text = "TIFF (Lossless Print)", enabled = true, onClick = { onExport(ExportType.TIFF) })
            ExportButton(text = "BMP (Uncompressed)", enabled = true, onClick = { onExport(ExportType.BMP) })

            Spacer(modifier = Modifier.height(16.dp))

            Text("External Editors", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            ExportButton(text = "PSD (Photoshop)", enabled = true, onClick = { onExport(ExportType.PSD) })
            ExportButton(text = "XCF (GIMP Project)", enabled = true, onClick = { onExport(ExportType.XCF) })
            
            Spacer(modifier = Modifier.height(64.dp))
        }
    }

    if (showWebpDialog) {
        WebPFormatDialog(
            onDismiss = { showWebpDialog = false },
            onConfirm = { type ->
                showWebpDialog = false
                val exportType = if (type == WebPType.LOSSY) ExportType.WEBP_LOSSY else ExportType.WEBP_LOSSLESS
                onExport(exportType)
            }
        )
    }
}

@Composable
fun ExportSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text = text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = modifier.padding(bottom = 8.dp))
}

@Composable
fun ExportButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(48.dp).padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.small
    ) { Text(text) }
}

@Composable
fun ResolutionOption(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(40.dp).selectable(selected = selected, onClick = onClick, role = Role.RadioButton).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(text = text, modifier = Modifier.padding(start = 12.dp), style = MaterialTheme.typography.bodyMedium)
    }
}
