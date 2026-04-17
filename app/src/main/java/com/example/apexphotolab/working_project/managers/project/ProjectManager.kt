package com.example.apexphotolab.working_project.managers.project

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.SettingsPersistence
import com.example.apexphotolab.working_project.util.layers.Layer
import com.example.apexphotolab.working_project.util.layers.LayerSerializer
import org.json.JSONArray
import org.json.JSONObject

/**
 * Job: Logic Worker (Specialist).
 * Responsibility: Low-level project file management, snapshot retrieval, and project lifecycle.
 */
object ProjectManager {

    fun loadLayers(context: Context, projectDir: DocumentFile): List<Layer> {
        val layersFile = projectDir.findFile("layers.json") ?: return emptyList()
        val jsonString = context.contentResolver.openInputStream(layersFile.uri)?.bufferedReader()?.use { it.readText() }
            ?: return emptyList()

        return try {
            val jsonArray = JSONArray(jsonString)
            val layers = mutableListOf<Layer>()
            for (i in 0 until jsonArray.length()) {
                layers.add(LayerSerializer.fromJson(jsonArray.getJSONObject(i)))
            }
            layers
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getHistory(projectDir: DocumentFile): List<DocumentFile> {
        return projectDir.listFiles()
            .filter { it.name?.startsWith("save_") == true && it.name?.endsWith(".json") == true }
            .sortedByDescending { it.lastModified() }
    }

    fun rollback(context: Context, projectDir: DocumentFile, snapshot: DocumentFile): Boolean {
        return try {
            val snapshotData = context.contentResolver.openInputStream(snapshot.uri)?.bufferedReader()?.use { it.readText() }
                ?: return false

            val layersFile = projectDir.findFile("layers.json") ?: projectDir.createFile("application/json", "layers.json")
            ?: return false

            context.contentResolver.openOutputStream(layersFile.uri, "wt")?.use { it.write(snapshotData.toByteArray()) }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun createProject(context: Context, projectName: String, bitmap: Bitmap): DocumentFile? {
        val rootUri = SettingsPersistence.getProjectDir(context) ?: return null
        val rootDir = DocumentFile.fromTreeUri(context, rootUri) ?: return null

        val projectDir = rootDir.createDirectory(projectName) ?: return null

        // 1. Save base image
        val baseImageFile = projectDir.createFile("image/png", "base.png") ?: return null
        context.contentResolver.openOutputStream(baseImageFile.uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        // 2. Create initial layer
        val baseLayer = Layer(
            id = "base",
            title = "Background",
            imageUri = baseImageFile.uri,
            zOrder = 0,
            width = bitmap.width,
            height = bitmap.height
        )

        // 3. Save initial project state
        ProjectSaveManager.saveProject(context, projectDir.uri, listOf(baseLayer), "Project Birth")

        return projectDir
    }

    fun getProjectFiles(context: Context): List<DocumentFile> {
        val rootUri = SettingsPersistence.getProjectDir(context) ?: return emptyList()
        val rootDir = DocumentFile.fromTreeUri(context, rootUri) ?: return emptyList()
        return rootDir.listFiles()
            .filter { it.isDirectory }
            .sortedByDescending { it.lastModified() }
    }

    fun addImageLayer(context: Context, projectDir: DocumentFile, title: String, bitmap: Bitmap): Layer? {
        return try {
            val fileName = "layer_${System.currentTimeMillis()}.png"
            val file = projectDir.createFile("image/png", fileName) ?: return null
            context.contentResolver.openOutputStream(file.uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Layer(
                id = System.currentTimeMillis().toString(),
                title = title,
                imageUri = file.uri,
                zOrder = 1,
                width = bitmap.width,
                height = bitmap.height
            )
        } catch (e: Exception) {
            null
        }
    }

    fun deleteSnapshots(snapshots: List<DocumentFile>): Boolean {
        var allDeleted = true
        snapshots.forEach {
            if (!it.delete()) allDeleted = false
        }
        return allDeleted
    }

    fun renameSnapshot(context: Context, snapshot: DocumentFile, newNote: String): Boolean {
        val name = snapshot.name ?: return false
        val parts = name.split("_")
        if (parts.size < 2) return false
        val timestamp = parts[1]
        val newName = "save_${timestamp}_${newNote}.json"
        return snapshot.renameTo(newName)
    }
}
