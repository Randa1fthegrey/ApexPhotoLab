package com.example.apexphotolab.the_build.working_project.managers.project

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.the_build.working_project.util.layers.Layer
import com.example.apexphotolab.the_build.working_project.util.layers.LayerSerializer
import org.json.JSONArray

/**
 * Job: Layer JSON Deserializer Specialist.
 * Responsibility: Reads and parses layers.json configuration files from project directories.
 */
object ProjectLayerLoader {

    fun loadLayers(context: Context, projectDir: DocumentFile): List<Layer> {
        val layersFile = projectDir.findFile(val_util.FILE_LAYERS_JSON) ?: return emptyList()
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
}
