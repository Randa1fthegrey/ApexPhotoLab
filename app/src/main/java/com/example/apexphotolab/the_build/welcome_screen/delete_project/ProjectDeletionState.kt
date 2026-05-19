package com.example.apexphotolab.the_build.welcome_screen.delete_project

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Job: Deletion Lifecycle Manager.
 * Responsibility: Manages the pending deletion state and coordinates with the deleter service.
 */
class ProjectDeletionState {
    var pendingDeletion by mutableStateOf<DocumentFile?>(null)
        private set

    fun setPending(file: DocumentFile?) {
        pendingDeletion = file
    }

    fun executeDeletion(
        context: Context,
        scope: CoroutineScope,
        onDeleted: () -> Unit
    ) {
        val project = pendingDeletion ?: return
        pendingDeletion = null

        scope.launch {
            if (ProjectDeleter.delete(context, project)) {
                onDeleted()
            }
        }
    }
}