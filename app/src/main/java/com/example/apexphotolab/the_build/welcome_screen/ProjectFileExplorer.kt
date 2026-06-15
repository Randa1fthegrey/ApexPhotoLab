package com.example.apexphotolab.the_build.welcome_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.the_build.welcome_screen.delete_project.ProjectDeletionState
import com.example.apexphotolab.the_build.welcome_screen.ui.ProjectItemRow
import com.example.apexphotolab.the_build.welcome_screen.ui.EmptyCategoryNotice
import com.example.apexphotolab.the_build.welcome_screen.ui.ProjectCategoryHeader
import com.example.apexphotolab.the_build.welcome_screen.delete_project.ui.DeleteConfirmDialog

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
    
    // State Trio
    val listState = remember { ProjectListState(context) }
    val categoryState: ProjectCategoryState = remember(listState.projectFiles) {
        ProjectCategoryState { listState.projectFiles }
    }
    val deletionState = remember { ProjectDeletionState() }

    deletionState.pendingDeletion?.let { project ->
        DeleteConfirmDialog(
            projectName = project.name ?: val_util.FALLBACK_PROJECT_NAME,
            onDismiss = { deletionState.setPending(null) },
            onConfirm = {
                deletionState.executeDeletion(context, scope) {
                    listState.refresh(context)
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
        item { ProjectCategoryHeader(val_util.CAT_STATIC) }
        if (categoryState.staticProjects.isEmpty()) {
            item { EmptyCategoryNotice() }
        } else {
            items(categoryState.staticProjects) { file ->
                ProjectItemRow(
                    file,
                    isDeleteMode,
                    onProjectSelected
                ) { deletionState.setPending(it) }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- ANIMATED SECTION ---
        item { ProjectCategoryHeader(val_util.CAT_ANIMATED) }
        if (categoryState.animatedProjects.isEmpty()) {
            item { EmptyCategoryNotice() }
        } else {
            items(categoryState.animatedProjects) { file ->
                ProjectItemRow(
                    file,
                    isDeleteMode,
                    onProjectSelected
                ) { deletionState.setPending(it) }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- SEQUENTIAL SECTION ---
        item { ProjectCategoryHeader(val_util.CAT_SEQUENTIAL) }
        if (categoryState.sequentialProjects.isEmpty()) {
            item { EmptyCategoryNotice() }
        } else {
            items(categoryState.sequentialProjects) { file ->
                ProjectItemRow(
                    file,
                    isDeleteMode,
                    onProjectSelected
                ) { deletionState.setPending(it) }
            }
        }
        
        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}
