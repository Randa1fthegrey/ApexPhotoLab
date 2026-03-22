package com.example.apexphotolab.workspace.toolbars.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.ProjectManager
import com.example.apexphotolab.ui.dialogs.RenameSnapshotDialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryPanel(
    modifier: Modifier = Modifier,
    projectDirUri: android.net.Uri,
    onRollback: (DocumentFile) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var history by remember { mutableStateOf<List<DocumentFile>>(emptyList()) }
    val projectDir = remember(projectDirUri) { DocumentFile.fromTreeUri(context, projectDirUri) }
    
    var snapshotToRename by remember { mutableStateOf<DocumentFile?>(null) }

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
                Text(
                    text = "🕑",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = "Project History",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
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

@Composable
fun SnapshotRow(
    snapshot: DocumentFile, 
    onRollback: () -> Unit,
    onRenameClick: () -> Unit
) {
    val timeFormatter = remember { SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault()) }
    
    val parts = snapshot.name?.removePrefix("save_")?.removeSuffix(".json")?.split("_")
    
    val timePart = if (parts != null && parts.size >= 2) "${parts[0]}_${parts[1]}" else null
    val notePart = if (parts != null && parts.size > 2) parts.drop(2).joinToString(" ") else "Manual Save"
    
    val isProjectBirth = notePart == "Project Birth"

    val date = timePart?.let {
        try {
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).parse(it)
        } catch (e: Exception) { null }
    }

    val displayTime = date?.let { timeFormatter.format(it) } ?: "Unknown Date"
    val fileSize = snapshot.length() / 1024 // KB

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notePart, 
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = displayTime, 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Size: ${fileSize} KB",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            // Only show the Rename (Pencil) button if it's NOT the Project Birth
            if (!isProjectBirth) {
                IconButton(onClick = onRenameClick) {
                    Text(text = "✏️", fontSize = 20.sp)
                }
            }

            IconButton(onClick = onRollback) {
                Text(text = "🕑", fontSize = 20.sp)
            }
        }
    }
}
