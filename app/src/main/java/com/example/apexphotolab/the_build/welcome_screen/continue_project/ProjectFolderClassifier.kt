package com.example.apexphotolab.the_build.welcome_screen.continue_project

import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Job: Single-Pass Classifier Specialist.
 * Responsibility: Classifies project folders into logical categories in 1 fast pass on Dispatchers.IO,
 * using instant in-memory name checks without executing SAF disk cursor scans.
 */
object ProjectFolderClassifier {

    data class Categories(
        val nesTileMapProjects: List<DocumentFile> = emptyList(),
        val staticProjects: List<DocumentFile> = emptyList(),
        val animatedProjects: List<DocumentFile> = emptyList(),
        val sequentialProjects: List<DocumentFile> = emptyList()
    )

    suspend fun classify(rawProjects: List<DocumentFile>): Categories = withContext(Dispatchers.IO) {
        val nesProjects = mutableListOf<DocumentFile>()
        val staticProjects = mutableListOf<DocumentFile>()

        for (dir in rawProjects) {
            val name = dir.name?.lowercase() ?: ""
            // Fast in-memory string matching (0.00ms) without SAF findFile() cursor scans
            val isNes = name.contains("nes") ||
                    name.contains("tile") ||
                    name.contains("bios") ||
                    name.contains("mapper") ||
                    name.contains("chr") ||
                    name.contains("nametable")

            if (isNes) {
                nesProjects.add(dir)
            } else {
                staticProjects.add(dir)
            }
        }

        Categories(
            nesTileMapProjects = nesProjects,
            staticProjects = staticProjects
        )
    }
}
