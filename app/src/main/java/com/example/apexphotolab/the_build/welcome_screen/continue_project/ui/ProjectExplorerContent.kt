package com.example.apexphotolab.the_build.welcome_screen.continue_project.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.the_build.welcome_screen.continue_project.ProjectFolderClassifier
import com.example.apexphotolab.the_build.welcome_screen.val_util

/**
 * Job: Pure List View Presenter.
 * Responsibility: Renders categorized project lists, headers, item rows, and empty notices.
 */
@Composable
fun ProjectExplorerContent(
    modifier: Modifier = Modifier,
    categories: ProjectFolderClassifier.Categories,
    isLoading: Boolean,
    isDeleteMode: Boolean,
    onProjectSelected: (DocumentFile) -> Unit,
    onPendingDelete: (DocumentFile) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // --- NES TILE MAP SECTION ---
        item { ProjectCategoryHeader(val_util.CAT_NES_TILE_MAP) }
        if (categories.nesTileMapProjects.isEmpty()) {
            if (!isLoading) item { EmptyCategoryNotice() }
        } else {
            items(categories.nesTileMapProjects) { file ->
                ProjectItemRow(file, isDeleteMode, onProjectSelected, onPendingDelete)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- STATIC SECTION ---
        item { ProjectCategoryHeader(val_util.CAT_STATIC) }
        if (categories.staticProjects.isEmpty()) {
            if (!isLoading) item { EmptyCategoryNotice() }
        } else {
            items(categories.staticProjects) { file ->
                ProjectItemRow(file, isDeleteMode, onProjectSelected, onPendingDelete)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- ANIMATED SECTION ---
        item { ProjectCategoryHeader(val_util.CAT_ANIMATED) }
        if (categories.animatedProjects.isEmpty()) {
            if (!isLoading) item { EmptyCategoryNotice() }
        } else {
            items(categories.animatedProjects) { file ->
                ProjectItemRow(file, isDeleteMode, onProjectSelected, onPendingDelete)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- SEQUENTIAL SECTION ---
        item { ProjectCategoryHeader(val_util.CAT_SEQUENTIAL) }
        if (categories.sequentialProjects.isEmpty()) {
            if (!isLoading) item { EmptyCategoryNotice() }
        } else {
            items(categories.sequentialProjects) { file ->
                ProjectItemRow(file, isDeleteMode, onProjectSelected, onPendingDelete)
            }
        }

        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}
