package com.example.apexphotolab.the_build.working_project.managers.project

import android.content.Context
import android.graphics.Bitmap
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.android.android_keys.SettingsPersistence
import com.example.apexphotolab.the_build.welcome_screen.new_project.ProjectType
import com.example.apexphotolab.the_build.working_project.util.layers.Layer

/**
 * Job: Project Directory Initialization Specialist.
 * Responsibility: Creates new project directories, initializes base image files, and saves birth snapshot state.
 */
object ProjectCreator {

    suspend fun createProject(
        context: Context,
        projectName: String,
        bitmap: Bitmap,
        projectType: ProjectType = ProjectType.STATIC
    ): DocumentFile? {
        val rootUri = SettingsPersistence.getProjectDir(context) ?: return null
        val rootDir = DocumentFile.fromTreeUri(context, rootUri) ?: return null

        val projectDir = rootDir.createDirectory(projectName) ?: return null

        // 0. Save project type marker
        if (projectType == ProjectType.NES_TILE_MAP) {
            val typeFile = projectDir.createFile("text/plain", "project_type.txt")
            typeFile?.let { file ->
                context.contentResolver.openOutputStream(file.uri)?.use { out ->
                    out.write(projectType.name.toByteArray())
                }
            }
        }

        // 1. Save base image
        val baseImageFile = projectDir.createFile(val_util.MIME_PNG, val_util.FILE_BASE_PNG) ?: return null
        context.contentResolver.openOutputStream(baseImageFile.uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        // 2. Create initial layer
        val baseLayer = Layer(
            id = val_util.LAYER_BASE_ID,
            title = val_util.LAYER_BASE_TITLE,
            imageUri = baseImageFile.uri,
            zOrder = 0,
            width = bitmap.width,
            height = bitmap.height
        )

        // 3. Save initial project state
        ProjectSaveManager.saveProject(context, projectDir.uri, listOf(baseLayer), val_util.SNAPSHOT_BIRTH)

        return projectDir
    }
}
