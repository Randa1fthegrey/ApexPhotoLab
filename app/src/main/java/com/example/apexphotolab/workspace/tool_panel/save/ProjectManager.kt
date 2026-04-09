package com.example.apexphotolab.workspace.tool_panel.save

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.SettingsManager
import com.example.apexphotolab.workspace.tool_panel.layers.Layer
import com.example.apexphotolab.workspace.tool_panel.layers.LayerSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * The core engine for project persistence.
 * Implements a Snapshot-based History system for rollbacks.
 */
object ProjectManager {
    const val PROJECT_FILE_NAME = "project.json"
    private const val IMAGES_DIR_NAME = "images"
    private const val SAVES_DIR_NAME = "saves"

    private fun getProjectsDir(context: Context): DocumentFile? {
        val customDirUri = SettingsManager.getCustomProjectDir(context)
        if (customDirUri != null) {
            val docFile = DocumentFile.fromTreeUri(context, customDirUri)
            if (docFile != null && docFile.canRead()) {
                return docFile
            }
        }
        return null
    }

    suspend fun createProject(context: Context, projectName: String, baseImageBitmap: Bitmap): DocumentFile? =
        withContext(Dispatchers.IO) {
            try {
                val projectsDir = getProjectsDir(context)
                if (projectsDir == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Please set a project directory first.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@withContext null
                }

                val newProjectDir = projectsDir.createDirectory(projectName)!!
                newProjectDir.createDirectory(IMAGES_DIR_NAME)!!
                newProjectDir.createDirectory(SAVES_DIR_NAME)!!

                val imagesDir = newProjectDir.findFile(IMAGES_DIR_NAME)!!
                val baseImageFile = imagesDir.createFile("image/png", "base_image.png")!!
                context.contentResolver.openOutputStream(baseImageFile.uri)?.use { output ->
                    baseImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                }

                val baseLayer = Layer(
                    id = "base",
                    title = "Base Image",
                    imageUri = baseImageFile.uri,
                    width = baseImageBitmap.width,
                    height = baseImageBitmap.height
                )

                // Perform initial save to create the birth of the project
                saveProject(context, newProjectDir, listOf(baseLayer), "Project Birth")

                newProjectDir
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error creating project", Toast.LENGTH_SHORT).show()
                }
                null
            }
        }

    suspend fun addImageLayer(context: Context, projectDir: DocumentFile, layerTitle: String, imageBitmap: Bitmap): Layer? =
        withContext(Dispatchers.IO) {
            try {
                val imagesDir = projectDir.findFile(IMAGES_DIR_NAME) ?: projectDir.createDirectory(
                    IMAGES_DIR_NAME
                )!!
                val newImageFile = imagesDir.createFile("image/png", "${UUID.randomUUID()}.png")!!

                context.contentResolver.openOutputStream(newImageFile.uri)?.use { output ->
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                }

                Layer(
                    id = UUID.randomUUID().toString(),
                    title = layerTitle,
                    imageUri = newImageFile.uri,
                    width = imageBitmap.width,
                    height = imageBitmap.height
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    suspend fun loadLayers(context: Context, projectDir: DocumentFile): List<Layer> = withContext(
        Dispatchers.IO
    ) {
        try {
            val projectFile =
                projectDir.findFile(PROJECT_FILE_NAME) ?: return@withContext emptyList()
            context.contentResolver.openInputStream(projectFile.uri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val jsonString = reader.readText()
                val json = JSONObject(jsonString)
                val layersJson = json.getJSONArray("layers")
                val layers = mutableListOf<Layer>()
                for (i in 0 until layersJson.length()) {
                    layers.add(LayerSerializer.fromJson(layersJson.getJSONObject(i)))
                }
                layers
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    } ?: emptyList()

    /**
     * Saves the project and creates a unique snapshot point in the history log.
     * @param title A user-provided note describing this save point.
     */
    suspend fun saveProject(context: Context, projectDir: DocumentFile, layers: List<Layer>, title: String) =
        withContext(Dispatchers.IO) {
            try {
                val layersJson = JSONArray()
                layers.forEach { layer -> layersJson.put(LayerSerializer.toJson(layer)) }
                val json = JSONObject().apply {
                    put("layers", layersJson)
                    put("timestamp", System.currentTimeMillis())
                    put("note", title)
                }
                val jsonString = json.toString(4)

                // 1. Write the snapshot to the project-specific history directory
                val savesDir = projectDir.findFile(SAVES_DIR_NAME) ?: projectDir.createDirectory(
                    SAVES_DIR_NAME
                )!!
                val timeStamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

                // Enforce 17 char limit and permit punctuation (except strictly forbidden file chars)
                val notePart = title.take(17).trim()
                val fileSafeNote = notePart.replace(Regex("[\\\\/:*?\"<>|]"), "_")
                val saveFileName =
                    if (fileSafeNote.isEmpty()) "save_$timeStamp.json" else "save_${timeStamp}_$fileSafeNote.json"

                val snapshotFile = savesDir.createFile("application/json", saveFileName)!!
                context.contentResolver.openOutputStream(snapshotFile.uri)
                    ?.use { it.write(jsonString.toByteArray()) }

                // 2. Update the main project.json file (The "Active" state)
                val projectFile = projectDir.findFile(PROJECT_FILE_NAME)
                    ?: projectDir.createFile("application/json", PROJECT_FILE_NAME)!!
                context.contentResolver.openOutputStream(projectFile.uri)
                    ?.use { it.write(jsonString.toByteArray()) }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Snapshot Created: $notePart", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error creating snapshot", Toast.LENGTH_SHORT).show()
                }
            }
        }

    /**
     * Renames a snapshot file and updates the note inside its JSON content.
     */
    suspend fun renameSnapshot(context: Context, snapshot: DocumentFile, newTitle: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                // 1. Read existing content to update the "note" inside the JSON
                val content = context.contentResolver.openInputStream(snapshot.uri)?.use {
                    it.bufferedReader().readText()
                } ?: return@withContext false

                val json = JSONObject(content)
                val notePart = newTitle.take(17).trim()
                json.put("note", notePart)
                val updatedContent = json.toString(4)

                // 2. Overwrite the file with updated JSON
                context.contentResolver.openOutputStream(snapshot.uri)?.use {
                    it.write(updatedContent.toByteArray())
                }

                // 3. Rename the actual file
                val nameParts = snapshot.name?.split("_") ?: return@withContext false
                if (nameParts.size < 3) return@withContext false
                val timeStampPart = "${nameParts[1]}_${nameParts[2].substringBefore(".json")}"

                val fileSafeNote = notePart.replace(Regex("[\\\\/:*?\"<>|]"), "_")
                val newFileName = "save_${timeStampPart}_$fileSafeNote.json"

                snapshot.renameTo(newFileName)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    /**
     * Deletes a batch of snapshots.
     */
    suspend fun deleteSnapshots(snapshots: List<DocumentFile>): Boolean =
        withContext(Dispatchers.IO) {
            var allDeleted = true
            snapshots.forEach { file ->
                if (!file.delete()) {
                    allDeleted = false
                }
            }
            allDeleted
        }

    /**
     * Retrieves the history of snapshots for this project.
     * Sorted chronologically: Oldest (Project Birth) at the top.
     */
    fun getHistory(projectDir: DocumentFile): List<DocumentFile> {
        val savesDir = projectDir.findFile(SAVES_DIR_NAME)
        return savesDir?.listFiles()
            ?.filter { it.name?.startsWith("save_") == true }
            ?.sortedBy { it.name } ?: emptyList()
    }

    /**
     * Rolls the project back to a specific snapshot point.
     */
    suspend fun rollback(context: Context, projectDir: DocumentFile, snapshot: DocumentFile): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val projectFile = projectDir.findFile(PROJECT_FILE_NAME)
                    ?: projectDir.createFile("application/json", PROJECT_FILE_NAME)!!

                context.contentResolver.openInputStream(snapshot.uri)?.use { input ->
                    context.contentResolver.openOutputStream(projectFile.uri)?.use { output ->
                        input.copyTo(output)
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    fun getProjectFiles(context: Context): List<DocumentFile> {
        val projectsDir = getProjectsDir(context)
        return projectsDir?.listFiles()?.filter { it.isDirectory } ?: emptyList()
    }
}