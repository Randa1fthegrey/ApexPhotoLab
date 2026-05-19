package com.example.apexphotolab.the_build.welcome_screen.new_project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Job: Logic/State Management.
 * Manages the multi-step state of the New Project wizard.
 */
class NewProjectWorkflowState {
    var showProjectTypeDialog by mutableStateOf(false)
    var showProjectNameDialog by mutableStateOf(false)
    var showCopyConfirmDialog by mutableStateOf(false)
    var projectName by mutableStateOf("")

    fun startFlow() {
        showProjectTypeDialog = true
    }

    fun onTypeSelected() {
        showProjectTypeDialog = false
        showProjectNameDialog = true
    }

    fun onNameConfirmed(name: String) {
        projectName = name
        showProjectNameDialog = false
        showCopyConfirmDialog = true
    }

    fun onConfirmed() {
        showCopyConfirmDialog = false
    }

    fun dismissAll() {
        showProjectTypeDialog = false
        showProjectNameDialog = false
        showCopyConfirmDialog = false
    }
}

@Composable
fun rememberNewProjectWorkflowState() = remember { NewProjectWorkflowState() }
