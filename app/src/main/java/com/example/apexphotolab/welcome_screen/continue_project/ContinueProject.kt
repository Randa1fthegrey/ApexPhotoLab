package com.example.apexphotolab.welcome_screen.continue_project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.welcome_screen.delete_project.DeleteConfirmDialog
import com.example.apexphotolab.welcome_screen.delete_project.ProjectDeleter
import com.example.apexphotolab.working_project.managers.project.ProjectManager
import kotlinx.coroutines.launch

/**
 * Job: Orchestrator.
 * Manages the state and layout assembly for the Project Explorer.
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
                    if (ProjectDeleter.delete(context, project)) {
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
