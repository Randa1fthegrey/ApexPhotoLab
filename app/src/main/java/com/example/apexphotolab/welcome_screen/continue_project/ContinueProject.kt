package com.example.apexphotolab.welcome_screen.continue_project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.workspace.tool_panel.save.ProjectManager
import com.example.apexphotolab.welcome_screen.delete_project.DeleteConfirmDialog
import com.example.apexphotolab.welcome_screen.delete_project.deleteProject
import kotlinx.coroutines.launch

/**
 * The Organized Project Explorer.
 * Updated: Standardized naming to "Sequential" for multi-page document projects.
 */
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

    // Logic to categorize projects (Placeholders for future metadata filtering)
    val staticProjects = projectFiles.sortedByDescending { it.name }
    val animatedProjects = emptyList<DocumentFile>()
    val sequentialProjects = emptyList<DocumentFile>()

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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // --- STATIC SECTION ---
        item { ProjectCategoryHeader("Static Projects") }
        if (staticProjects.isEmpty()) {
            item { EmptyCategoryNotice() }
        } else {
            items(staticProjects) { file ->
                ProjectItemRow(file, isDeleteMode, onProjectSelected) { showDeleteConfirmDialog = it }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- ANIMATED SECTION ---
        item { ProjectCategoryHeader("Animated Projects") }
        if (animatedProjects.isEmpty()) {
            item { EmptyCategoryNotice() }
        } else {
            items(animatedProjects) { file ->
                ProjectItemRow(file, isDeleteMode, onProjectSelected) { showDeleteConfirmDialog = it }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- SEQUENTIAL SECTION ---
        item { ProjectCategoryHeader("Sequential Projects") }
        if (sequentialProjects.isEmpty()) {
            item { EmptyCategoryNotice() }
        } else {
            items(sequentialProjects) { file ->
                ProjectItemRow(file, isDeleteMode, onProjectSelected) { showDeleteConfirmDialog = it }
            }
        }
        
        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}

@Composable
fun ProjectCategoryHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyCategoryNotice() {
    Text(
        text = "No projects in this category.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun ProjectItemRow(
    file: DocumentFile,
    isDeleteMode: Boolean,
    onProjectSelected: (DocumentFile) -> Unit,
    onDeleteRequest: (DocumentFile) -> Unit
) {
    Text(
        text = file.name ?: "Unknown Project",
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                if (isDeleteMode) onDeleteRequest(file) else onProjectSelected(file)
            }
            .padding(16.dp)
    )
}
