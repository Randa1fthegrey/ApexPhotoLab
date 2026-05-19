package com.example.apexphotolab.the_build.working_project.tool_panel.save

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.the_build.working_project.managers.project.ProjectManager
import com.example.apexphotolab.the_build.working_project.tool_panel.save.dialogs.RenameSnapshotDialog
import kotlinx.coroutines.launch

/**
 * Job: UI Navigator (History).
 * Responsibility: Manages the high-level state of the project history panel, 
 * including listing snapshots and coordinating deletion mode.
 */
@Composable
fun HistoryPanel(
    modifier: Modifier = Modifier,
    projectDirUri: Uri,
    onRollback: (DocumentFile) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var history by remember { mutableStateOf<List<DocumentFile>>(emptyList()) }
    val projectDir = remember(projectDirUri) { DocumentFile.fromTreeUri(context, projectDirUri) }
    
    var snapshotToRename by remember { mutableStateOf<DocumentFile?>(null) }
    
    // DELETE MODE STATE
    var isDeleteMode by remember { mutableStateOf(false) }
    val selectedSnapshots = remember { mutableStateListOf<DocumentFile>() }

    fun refreshHistory() {
        projectDir?.let {
            history = ProjectManager.getHistory(it)
        }
    }

    LaunchedEffect(projectDir) {
        refreshHistory()
    }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Tools")
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Project History",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 4.dp).size(20.dp)
                    )
                }
                
                // THE RED X BUTTON
                IconButton(
                    onClick = {
                        if (isDeleteMode) {
                            if (selectedSnapshots.isNotEmpty()) {
                                scope.launch {
                                    val success = ProjectManager.deleteSnapshots(selectedSnapshots.toList())
                                    if (success) {
                                        Toast.makeText(context, "Saves deleted", Toast.LENGTH_SHORT).show()
                                    }
                                    selectedSnapshots.clear()
                                    isDeleteMode = false
                                    refreshHistory()
                                }
                            } else {
                                isDeleteMode = false
                            }
                        } else {
                            isDeleteMode = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = if (isDeleteMode) "Confirm Delete" else "Enter Delete Mode",
                        tint = if (!isDeleteMode || selectedSnapshots.isNotEmpty()) Color.Red else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No history found", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(history) { snapshot ->
                        SnapshotRow(
                            snapshot = snapshot,
                            isDeleteMode = isDeleteMode,
                            isSelected = selectedSnapshots.contains(snapshot),
                            onToggleSelection = { selected ->
                                if (selected) selectedSnapshots.add(snapshot)
                                else selectedSnapshots.remove(snapshot)
                            },
                            onRollback = { onRollback(snapshot) },
                            onRenameClick = { snapshotToRename = snapshot }
                        )
                    }
                }
            }
        }
    }

    snapshotToRename?.let { snapshot ->
        val currentNote = snapshot.name?.removePrefix("save_")?.removeSuffix(".json")?.split("_")?.drop(2)?.joinToString(" ") ?: ""
        RenameSnapshotDialog(
            initialName = currentNote,
            onDismiss = { snapshotToRename = null },
            onConfirm = { newName ->
                snapshotToRename = null
                scope.launch {
                    if (ProjectManager.renameSnapshot(context, snapshot, newName)) {
                        refreshHistory()
                    }
                }
            }
        )
    }
}

