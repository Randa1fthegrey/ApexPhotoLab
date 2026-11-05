package com.example.apexphotolab.the_build.working_project.managers.project

import android.content.Context
import android.graphics.Bitmap
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.the_build.working_project.util.layers.Layer

/**
 * Job: Image Layer Writer Specialist.
 * Responsibility: Writes new bitmap image files into project directories and returns new Layer instances.
 */
object ProjectLayerWriter {

    fun addImageLayer(context: Context, projectDir: DocumentFile, title: String, bitmap: Bitmap): Layer? {
        return try {
            val fileName = "layer_${System.currentTimeMillis()}.png"
            val file = projectDir.createFile(val_util.MIME_PNG, fileName) ?: return null
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
}
