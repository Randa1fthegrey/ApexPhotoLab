package com.example.apexphotolab

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.workspace.toolbars.layers.Layer
import com.example.apexphotolab.workspace.toolbars.layers.LayerSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

object ProjectManager {
    const val PROJECT_FILE_NAME = "project.json"
    private const val IMAGES_DIR_NAME = "images"

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

    suspend fun createProject(context: Context, projectName: String, baseImageBitmap: Bitmap): DocumentFile? = withContext(Dispatchers.IO) {
        try {
            val projectsDir = getProjectsDir(context) 
            if (projectsDir == null) {
                 withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Please set a project directory first.", Toast.LENGTH_SHORT).show()
                }
                return@withContext null
            }

            val newProjectDir = projectsDir.createDirectory(projectName)!!
            val imagesDir = newProjectDir.createDirectory(IMAGES_DIR_NAME)!!

            val baseImageFile = imagesDir.createFile("image/png", "base_image.png")!!
            context.contentResolver.openOutputStream(baseImageFile.uri)?.use { output ->
                baseImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            }

            val baseLayer = Layer(
                id = "base",
                title = "Base Image",
                imageUri = baseImageFile.uri
            )

            val projectFile = newProjectDir.createFile("application/json", PROJECT_FILE_NAME)!!
            context.contentResolver.openOutputStream(projectFile.uri)?.use { outputStream ->
                val layersJson = JSONArray()
                layersJson.put(LayerSerializer.toJson(baseLayer))
                val json = JSONObject().apply {
                    put("layers", layersJson)
                }
                outputStream.write(json.toString(4).toByteArray())
            }
            newProjectDir
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error creating project", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

    suspend fun addImageLayer(context: Context, projectDir: DocumentFile, layerTitle: String, imageBitmap: Bitmap): Layer? = withContext(Dispatchers.IO) {
        try {
            val imagesDir = projectDir.findFile(IMAGES_DIR_NAME) ?: projectDir.createDirectory(IMAGES_DIR_NAME)!!
            val newImageFile = imagesDir.createFile("image/png", "${UUID.randomUUID()}.png")!!

            context.contentResolver.openOutputStream(newImageFile.uri)?.use { output ->
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            }

            Layer(
                id = UUID.randomUUID().toString(),
                title = layerTitle,
                imageUri = newImageFile.uri
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun loadLayers(context: Context, projectDir: DocumentFile): List<Layer> = withContext(Dispatchers.IO) {
        try {
            val projectFile = projectDir.findFile(PROJECT_FILE_NAME) ?: return@withContext emptyList()
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

    suspend fun saveProject(context: Context, projectDir: DocumentFile, layers: List<Layer>) = withContext(Dispatchers.IO) {
        try {
            val projectFile = projectDir.findFile(PROJECT_FILE_NAME) ?: projectDir.createFile("application/json", PROJECT_FILE_NAME)!!
            
            context.contentResolver.openOutputStream(projectFile.uri)?.use { outputStream ->
                val layersJson = JSONArray()
                layers.forEach { layer -> layersJson.put(LayerSerializer.toJson(layer)) }
                val json = JSONObject().apply {
                    put("layers", layersJson)
                }
                outputStream.write(json.toString(4).toByteArray())
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Project Saved!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error saving project", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getProjectFiles(context: Context): List<DocumentFile> {
        val projectsDir = getProjectsDir(context)
        return projectsDir?.listFiles()?.filter { it.isDirectory } ?: emptyList()
    }
}
