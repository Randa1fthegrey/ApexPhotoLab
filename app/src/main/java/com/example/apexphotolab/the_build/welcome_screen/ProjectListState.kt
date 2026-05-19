package com.example.apexphotolab.the_build.welcome_screen

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.apexphotolab.the_build.working_project.managers.project.ProjectManager

/**
 * Job: Project Source Manager.
 * Responsibility: Fetches and holds the raw list of project files from storage.
 */
class ProjectListState(context: Context) {
    var projectFiles by mutableStateOf(ProjectManager.getProjectFiles(context))
        private set

    fun refresh(context: Context) {
        projectFiles = ProjectManager.getProjectFiles(context)
    }
}