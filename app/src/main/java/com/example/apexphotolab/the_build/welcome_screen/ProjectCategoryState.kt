package com.example.apexphotolab.the_build.welcome_screen

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.documentfile.provider.DocumentFile

/**
 * Job: Categorization Engine.
 * Responsibility: Sorts and filters projects into logical categories (Static, Animated, Sequential).
 */
class ProjectCategoryState(projectList: () -> List<DocumentFile>) {
    val staticProjects by derivedStateOf {
        projectList().sortedByDescending { it.name }
    }

    val animatedProjects by derivedStateOf {
        emptyList<DocumentFile>()
    }

    val sequentialProjects by derivedStateOf {
        emptyList<DocumentFile>()
    }
}