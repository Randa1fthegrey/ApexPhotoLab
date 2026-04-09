package com.example.apexphotolab.welcome_screen

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
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
import com.example.apexphotolab.workspace.tool_panel.save.ProjectManager
import com.example.apexphotolab.SettingsManager
import com.example.apexphotolab.workspace.util.BitmapUtils
import com.example.apexphotolab.welcome_screen.new_project.CopyConfirmDialog
import com.example.apexphotolab.welcome_screen.new_project.ProjectNameDialog
import com.example.apexphotolab.welcome_screen.new_project.ProjectType
import com.example.apexphotolab.welcome_screen.new_project.ProjectTypeDialog
import com.example.apexphotolab.welcome_screen.theme.ThemeSwitcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    
    // --- DIALOG STATES ---
    var showProjectTypeDialog by remember { mutableStateOf(false) }
    var showProjectNameDialog by remember { mutableStateOf(false) }
    var showCopyConfirmDialog by remember { mutableStateOf(false) }
    
    var projectName by remember { mutableStateOf("") }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
             uri?.let {
                scope.launch {
                    val bitmap = withContext(Dispatchers.IO) {
                        BitmapUtils.decodeCorrectedBitmap(context, it)
                    }
                    val newProjectDir = bitmap?.let { bm ->
                        ProjectManager.createProject(context, projectName, bm)
                    }
                    withContext(Dispatchers.Main) {
                        newProjectDir?.let { dir -> onStartProject(dir.uri) }
                    }
                }
            }
        }
    )

    // 1. SELECT TYPE
    if (showProjectTypeDialog) {
        ProjectTypeDialog(
            onDismiss = { showProjectTypeDialog = false },
            onConfirm = { type ->
                showProjectTypeDialog = false
                if (type == ProjectType.STATIC) {
                    showProjectNameDialog = true
                }
            }
        )
    }

    // 2. NAME PROJECT
    if (showProjectNameDialog) {
        ProjectNameDialog(
            onDismiss = { showProjectNameDialog = false },
            onConfirm = { name ->
                projectName = name
                showProjectNameDialog = false
                showCopyConfirmDialog = true
            }
        )
    }

    // 3. CONFIRM & PICK IMAGE
    if (showCopyConfirmDialog) {
        CopyConfirmDialog(
            projectName = projectName,
            onDismiss = { showCopyConfirmDialog = false },
            onConfirm = {
                showCopyConfirmDialog = false
                pickImageLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
    }

    val pickProjectDirLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { uri: Uri? ->
            uri?.let {
                SettingsManager.setCustomProjectDir(context, it)
                onProjectDirSet()
                Toast.makeText(context, "Custom project folder set!", Toast.LENGTH_SHORT).show()
            }
        }
    )

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
            // TRIGGER FLOW
            Button(
                onClick = { showProjectTypeDialog = true }, 
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ThemeSwitcher(label = "Light", checked = useDarkTheme == false) {
                    onThemeChange(false)
                }
                Spacer(modifier = Modifier.width(16.dp))
                ThemeSwitcher(label = "Dark", checked = useDarkTheme == true) {
                    onThemeChange(true)
                }
                Spacer(modifier = Modifier.width(16.dp))
                ThemeSwitcher(label = "System", checked = useDarkTheme == null) {
                    onThemeChange(null)
                }
            }
        }
    }
}
