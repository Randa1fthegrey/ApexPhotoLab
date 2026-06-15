package com.example.apexphotolab.the_build.working_project.managers.project

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.android.android_keys.SettingsPersistence
import com.example.apexphotolab.the_build.working_project.util.layers.Layer
import com.example.apexphotolab.the_build.working_project.managers.project.ProjectLoadManager
import com.example.apexphotolab.the_build.working_project.managers.project.ProjectManager
import com.example.apexphotolab.the_build.working_project.managers.project.ProjectSaveManager

/**
 * Job: Persistence/IO Worker.
 * Handles Project Loading, Saving, and History Rollbacks.
 * Pure logic/IO; NO UI side effects (Toasts).
 */
class ProjectPersistenceManager {
    var projectDirName by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(true)
        private set
    var lastSaveTime by mutableLongStateOf(System.currentTimeMillis())
        private set

    suspend fun loadProject(
        context: Context,
        projectDirUri: Uri,
        layers: SnapshotStateList<Layer>
    ): Boolean {
        isLoading = true
        val result = ProjectLoadManager.loadProject(context, projectDirUri)
        return if (result != null) {
            projectDirName = result.projectName
            layers.clear()
            val initializedLayers = result.layers.map { layer ->
                if (layer.id == val_util.LAYER_BASE_ID) layer.copy(isLocked = true) else layer
            }
            layers.addAll(initializedLayers)
            updateSaveTime()
            isLoading = false
            true
        } else {
            isLoading = false
            false
        }
    }

    suspend fun performQuickSave(
        context: Context,
        projectDirUri: Uri,
        layers: List<Layer>
    ): Boolean {
        val documentFile = DocumentFile.fromTreeUri(context, projectDirUri) ?: return false
        val history = ProjectManager.getHistory(documentFile)
        val quickSaveCount = history.count { snapshot ->
            snapshot.name?.contains(projectDirName) == true && !snapshot.name!!.contains(val_util.SNAPSHOT_BIRTH)
        }
        val autoTitle = if (quickSaveCount == 0) projectDirName else "$projectDirName ($quickSaveCount)"

        return try {
            ProjectSaveManager.saveProject(context, projectDirUri, layers, autoTitle)
            updateSaveTime()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun rollback(
        context: Context,
        projectDirUri: Uri,
        snapshot: DocumentFile,
        layers: SnapshotStateList<Layer>
    ): Boolean {
        val documentFile = DocumentFile.fromTreeUri(context, projectDirUri) ?: return false
        val success = ProjectManager.rollback(context, documentFile, snapshot)
        if (success) {
            layers.clear()
            layers.addAll(ProjectManager.loadLayers(context, documentFile))
            updateSaveTime()
            return true
        }
        return false
    }

    fun updateSaveTime() {
        lastSaveTime = System.currentTimeMillis()
    }

    fun setSaveConfirmationPreference(context: Context, show: Boolean) {
        SettingsPersistence.setShouldShowSaveConfirmation(context, show)
    }
}