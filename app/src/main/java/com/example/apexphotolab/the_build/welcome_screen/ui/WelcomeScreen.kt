package com.example.apexphotolab.the_build.welcome_screen.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.apexphotolab.the_build.welcome_screen.WelcomeActionHandlers
import com.example.apexphotolab.the_build.welcome_screen.new_project.ui.CopyConfirmDialog
import com.example.apexphotolab.the_build.welcome_screen.new_project.ui.ProjectNameDialog
import com.example.apexphotolab.the_build.welcome_screen.new_project.ProjectType
import com.example.apexphotolab.the_build.welcome_screen.new_project.ui.ProjectTypeDialog
import com.example.apexphotolab.the_build.welcome_screen.new_project.rememberNewProjectWorkflowState

/**
 * Job: UI Rendering (The Orchestrator).
 * Layouts the Welcome Screen and coordinates between the Workflow State and Action Handlers.
 */
@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    useDarkTheme: Boolean?,
    onThemeChange: (Boolean?) -> Unit,
    hasValidProjectDir: Boolean,
    onContinueProject: () -> Unit,
    onDeleteProject: () -> Unit,
    onStartProject: (Uri) -> Unit,
    onProjectDirSet: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- WORKERS ---
    val workflow = rememberNewProjectWorkflowState()
    val handlers = remember(context, scope, onStartProject, onProjectDirSet) {
        WelcomeActionHandlers(context, scope, onStartProject, onProjectDirSet)
    }

    // --- LAUNCHERS ---
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> handlers.handleImageResult(uri, workflow.projectName) }
    )

    val pickProjectDirLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { uri -> handlers.handleProjectDirResult(uri) }
    )

    // --- DIALOGS (WIZARD FLOW) ---
    if (workflow.showProjectTypeDialog) {
        ProjectTypeDialog(
            onDismiss = { workflow.dismissAll() },
            onConfirm = { type ->
                if (type == ProjectType.STATIC) workflow.onTypeSelected()
            }
        )
    }

    if (workflow.showProjectNameDialog) {
        ProjectNameDialog(
            onDismiss = { workflow.dismissAll() },
            onConfirm = { name -> workflow.onNameConfirmed(name) }
        )
    }

    if (workflow.showCopyConfirmDialog) {
        CopyConfirmDialog(
            projectName = workflow.projectName,
            onDismiss = { workflow.dismissAll() },
            onConfirm = {
                workflow.onConfirmed()
                pickImageLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
    }

    // --- UI LAYOUT ---
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text("Graphic coming soon!", textAlign = TextAlign.Center)
        }

        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { workflow.startFlow() },
                enabled = hasValidProjectDir
            ) {
                Text("Start New Project")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onContinueProject, enabled = hasValidProjectDir) {
                Text("Continue Project")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onDeleteProject, enabled = hasValidProjectDir) {
                Text("Delete Project")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { pickProjectDirLauncher.launch(null) }) {
                Text("Set Custom Project Folder")
            }

            Spacer(modifier = Modifier.height(32.dp))

            ThemeSelectionRow(useDarkTheme, onThemeChange)
        }
    }
}

@Composable
private fun ThemeSelectionRow(
    useDarkTheme: Boolean?,
    onThemeChange: (Boolean?) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        ThemeSwitcher(
            label = "Light",
            checked = useDarkTheme == false,
            onCheckedChange = { onThemeChange(false) }
        )
        Spacer(modifier = Modifier.width(16.dp))
        ThemeSwitcher(
            label = "Dark",
            checked = useDarkTheme == true,
            onCheckedChange = { onThemeChange(true) }
        )
        Spacer(modifier = Modifier.width(16.dp))
        ThemeSwitcher(
            label = "System",
            checked = useDarkTheme == null,
            onCheckedChange = { onThemeChange(null) }
        )
    }
}
