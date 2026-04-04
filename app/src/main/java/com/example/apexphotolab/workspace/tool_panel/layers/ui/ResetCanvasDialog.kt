package com.example.apexphotolab.workspace.tool_panel.layers.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.apexphotolab.workspace.tool_panel.layers.Layer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetCanvasDialog(
    layers: List<Layer>,
    onDismiss: () -> Unit,
    onRecenter: (String?) -> Unit,
    onReshape: (String?) -> Unit,
    onBoth: (String?) -> Unit
) {
    var layerName by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Reset Canvas",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                // --- SECTION A: GLOBAL ACTIONS ---
                Text(
                    text = "Global Actions (All Layers)",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onRecenter(null) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Recenter All")
                        }
                        Button(
                            onClick = { onReshape(null) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reshape All")
                        }
                    }
                    Button(
                        onClick = { onBoth(null) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Total Reset (Both)")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // --- SECTION B: TARGETED ACTIONS ---
                Text(
                    text = "Targeted Actions",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { /* Only handle expansion via the arrow button */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = layerName,
                        onValueChange = { 
                            layerName = it 
                            if (expanded) expanded = false // Close menu if user starts typing
                        },
                        label = { Text("Select Layer") },
                        placeholder = { Text("Search or select...") },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth(),
                        singleLine = true,
                        readOnly = expanded,
                        trailingIcon = {
                            IconButton(onClick = { 
                                if (!expanded) {
                                    keyboardController?.hide()
                                    focusManager.clearFocus(force = true)
                                }
                                expanded = !expanded
                            }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        layers.forEach { layer ->
                            DropdownMenuItem(
                                text = { Text(layer.title) },
                                onClick = {
                                    layerName = layer.title
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(
                        onClick = { onRecenter(layerName) },
                        enabled = layerName.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("Recenter", style = MaterialTheme.typography.labelSmall)
                    }
                    Button(
                        onClick = { onReshape(layerName) },
                        enabled = layerName.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("Reshape", style = MaterialTheme.typography.labelSmall)
                    }
                    Button(
                        onClick = { onBoth(layerName) },
                        enabled = layerName.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("Both", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    }
}
