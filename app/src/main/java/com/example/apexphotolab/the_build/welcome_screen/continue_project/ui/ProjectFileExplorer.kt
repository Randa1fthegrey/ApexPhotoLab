package com.example.apexphotolab.the_build.welcome_screen.continue_project.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.the_build.welcome_screen.continue_project.ProjectListLoader
import com.example.apexphotolab.the_build.welcome_screen.continue_project.ProjectListState
import com.example.apexphotolab.the_build.welcome_screen.delete_project.ProjectDeletionState
import com.example.apexphotolab.the_build.welcome_screen.delete_project.ui.DeleteConfirmDialog
import com.example.apexphotolab.the_build.welcome_screen.val_util

/**
 * Job: UI Layout Orchestrator.
 * Responsibility: Orchestrates UI layout components, loading indicator, and delete confirmation dialogs for the Project Explorer.
 */
@Composable
fun ProjectFileExplorer(
    modifier: Modifier = Modifier,
    isDeleteMode: Boolean,
    onProjectSelected: (DocumentFile) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state = remember { ProjectListState() }
    val loader = remember(context, scope) { ProjectListLoader(context, scope) }
    val deletionState = remember { ProjectDeletionState() }

    LaunchedEffect(Unit) {
        loader.load(state)
    }

    deletionState.pendingDeletion?.let { project ->
        DeleteConfirmDialog(
            projectName = project.name ?: val_util.FALLBACK_PROJECT_NAME,
            onDismiss = { deletionState.setPending(null) },
            onConfirm = {
                deletionState.executeDeletion(context, scope) {
                    loader.load(state)
                }
            }
        )
    }

    ProjectExplorerContent(
        modifier = modifier,
        categories = state.categories,
        isLoading = state.isLoading,
        isDeleteMode = isDeleteMode,
        onProjectSelected = onProjectSelected,
        onPendingDelete = { deletionState.setPending(it) }
    )
}
