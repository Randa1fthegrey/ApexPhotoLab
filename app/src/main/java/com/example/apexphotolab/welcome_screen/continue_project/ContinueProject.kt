package com.example.apexphotolab.welcome_screen.continue_project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.ProjectManager
import com.example.apexphotolab.welcome_screen.delete_project.DeleteConfirmDialog
import com.example.apexphotolab.welcome_screen.delete_project.deleteProject
import kotlinx.coroutines.launch

@Composable
fun ProjectFileExplorer(
    modifier: Modifier = Modifier,
    isDeleteMode: Boolean,
    onProjectSelected: (DocumentFile) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var projectFiles by remember { mutableStateOf(ProjectManager.getProjectFiles(context)) }
    var showDeleteConfirmDialog by remember { mutableStateOf<DocumentFile?>(null) }

    showDeleteConfirmDialog?.let { project ->
        DeleteConfirmDialog(
            projectName = project.name ?: "this project",
            onDismiss = { showDeleteConfirmDialog = null },
            onConfirm = {
                showDeleteConfirmDialog = null
                scope.launch {
                    if (deleteProject(context, project)) {
                        projectFiles = ProjectManager.getProjectFiles(context)
                    }
                }
            }
        )
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        if (projectFiles.isEmpty()) {
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No projects found.")
                }
            }
        } else {
            items(projectFiles) { file ->
                Text(
                    text = file.name ?: "Unknown File",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            if(isDeleteMode) {
                                showDeleteConfirmDialog = file
                            } else {
                                onProjectSelected(file)
                            }
                         }
                        .padding(16.dp)
                )
            }
        }
    }
}
