package com.example.apexphotolab.the_build.working_project.managers.project

import android.content.Context
import androidx.documentfile.provider.DocumentFile

/**
 * Job: Snapshot Rollback Execution Specialist.
 * Responsibility: Overwrites active layers.json configuration with selected snapshot data.
 */
object ProjectSnapshotRollback {

    fun rollback(context: Context, projectDir: DocumentFile, snapshot: DocumentFile): Boolean {
        return try {
            val snapshotData = context.contentResolver.openInputStream(snapshot.uri)?.bufferedReader()?.use { it.readText() }
                ?: return false

            val layersFile = projectDir.findFile(val_util.FILE_LAYERS_JSON) ?: projectDir.createFile(val_util.MIME_JSON, val_util.FILE_LAYERS_JSON)
            ?: return false

            context.contentResolver.openOutputStream(layersFile.uri, "wt")?.use { it.write(snapshotData.toByteArray()) }
            true
        } catch (e: Exception) {
            false
        }
    }
}
