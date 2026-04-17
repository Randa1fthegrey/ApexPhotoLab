package com.example.apexphotolab.working_project.managers.project

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.working_project.util.layers.Layer
import com.example.apexphotolab.working_project.util.layers.LayerSerializer
import org.json.JSONArray

/**
 * Job: Logic Worker (Specialist).
 * Responsibility: Handling the creation of project snapshots and updating the current state.
 */
object ProjectSaveManager {

    suspend fun saveProject(
        context: Context,
        projectDirUri: Uri,
        layers: List<Layer>,
        title: String
    ): Boolean {
        val projectDir = DocumentFile.fromTreeUri(context, projectDirUri) ?: return false

        val jsonArray = JSONArray()
        layers.forEach { layer ->
            jsonArray.put(LayerSerializer.toJson(layer))
        }
        val jsonData = jsonArray.toString()

        return try {
            // 1. Create a timestamped snapshot
            val timestamp = System.currentTimeMillis()
            val snapshotName = "save_${timestamp}_${title}.json"
            val snapshotFile = projectDir.createFile("application/json", snapshotName) ?: return false
            context.contentResolver.openOutputStream(snapshotFile.uri)?.use { it.write(jsonData.toByteArray()) }

            // 2. Update/Overwrite the "Current State" file (layers.json)
            val currentStateFile = projectDir.findFile("layers.json") ?: projectDir.createFile("application/json", "layers.json")
            ?: return false
            context.contentResolver.openOutputStream(currentStateFile.uri, "wt")?.use { it.write(jsonData.toByteArray()) }

            true
        } catch (e: Exception) {
            false
        }
    }
}
