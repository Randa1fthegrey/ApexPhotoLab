package com.example.apexphotolab.workspace

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.workspace.tool_panel.ToolDrawer
import com.example.apexphotolab.workspace.tool_panel.layers.ui.LayerNameDialog
import com.example.apexphotolab.workspace.tool_panel.save.ui.SaveConfirmDialog
import com.example.apexphotolab.workspace.tool_panel.save.ui.SnapshotNameDialog
import com.example.apexphotolab.workspace.tool_panel.export.data.ExportJobManager
import com.example.apexphotolab.workspace.tool_panel.export.ui.ExportProgressDialog
import com.example.apexphotolab.workspace.tool_panel.export.ui.ExportScreen
import com.example.apexphotolab.workspace.tool_panel.export.data.ExportType
import com.example.apexphotolab.workspace.tool_panel.filters.FilterPanel
import com.example.apexphotolab.workspace.tool_panel.layers.Layer
import com.example.apexphotolab.workspace.tool_panel.layers.LayersPanel
import com.example.apexphotolab.workspace.tool_panel.save.HistoryPanel
import com.example.apexphotolab.workspace.tool_panel.layers.NewLayerManager
import com.example.apexphotolab.workspace.tool_panel.save.ProjectLoadManager
import com.example.apexphotolab.workspace.tool_panel.save.ProjectManager
import com.example.apexphotolab.workspace.tool_panel.save.ProjectSaveManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun MainEditorScreen(
    modifier: Modifier = Modifier,
    projectDirUri: Uri,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val layers = remember { mutableStateListOf<Layer>() }
    var projectDirName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // --- UI STATE ---
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var activeToolIcon by remember { mutableStateOf(Icons.Default.Build) }
    var lastSaveTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val activeLayerName by remember { derivedStateOf { layers.lastOrNull { it.isVisible }?.title ?: "Background" } }
    
    // Panel/Screen States
    var showLayersPanel by remember { mutableStateOf(false) }
    var showExportScreen by remember { mutableStateOf(false) }
    var showFilterPanel by remember { mutableStateOf(false) }
    var showHistoryPanel by remember { mutableStateOf(false) }
    
    var colorFilter: ColorFilter? by remember { mutableStateOf(null) }
    var showSaveConfirmDialog by remember { mutableStateOf(false) }
    var showLayerNameDialog by remember { mutableStateOf<Uri?>(null) }
    var showSnapshotNameDialog by remember { mutableStateOf(false) }
    
    // Export Execution State
    var pendingExportType by remember { mutableStateOf<ExportType?>(null) }
    var showExportProgress by remember { mutableStateOf(false) }
    var exportProgress by remember { mutableStateOf(0f) }
    var exportJob by remember { mutableStateOf<Job?>(null) }

    // Helper to update tool and keep drawer open if options are needed
    val onToolSelected: (ImageVector, () -> Unit) -> Unit = { icon, action ->
        activeToolIcon = icon
        action()
    }

    // --- QUICK SAVE LOGIC ---
    val performQuickSave: () -> Unit = {
        scope.launch {
            val history = ProjectManager.getHistory(DocumentFile.fromTreeUri(context, projectDirUri)!!)
            // Filter history to find existing quick saves with the same project name
            val quickSaveCount = history.count { snapshot ->
                snapshot.name?.contains(projectDirName) == true && !snapshot.name!!.contains("Project Birth")
            }
            
            val autoTitle = if (quickSaveCount == 0) projectDirName else "$projectDirName ($quickSaveCount)"
            
            ProjectSaveManager.saveProject(context, projectDirUri, layers.toList(), autoTitle)
            lastSaveTime = System.currentTimeMillis()
            Toast.makeText(context, "Saved project!", Toast.LENGTH_SHORT).show()
        }
    }

    // --- LOAD LOGIC ---
    LaunchedEffect(projectDirUri) {
        isLoading = true
        val result = ProjectLoadManager.loadProject(context, projectDirUri)
        if (result != null) {
            projectDirName = result.projectName
            layers.clear()
            layers.addAll(result.layers)
            lastSaveTime = System.currentTimeMillis()
        } else {
            onNavigateBack()
        }
        isLoading = false
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> 
        uri?.let { showLayerNameDialog = it } 
    }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { dirUri ->
        dirUri?.let { directoryUri ->
            val exportType = pendingExportType ?: return@let
            pendingExportType = null
            showExportProgress = true
            exportJob = scope.launch {
                try {
                    ExportJobManager.runExportJob(context, exportType, directoryUri, projectDirName, layers, colorFilter != null) { exportProgress = it }
                    Toast.makeText(context, "Export Finished", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    if (e !is CancellationException) Toast.makeText(context, "Export Failed", Toast.LENGTH_SHORT).show()
                } finally { showExportProgress = false }
            }
        }
    }

    BackHandler {
        when {
            drawerState.isOpen -> {
                if (showLayersPanel || showExportScreen || showFilterPanel || showHistoryPanel) {
                    showLayersPanel = false; showExportScreen = false; showFilterPanel = false; showHistoryPanel = false
                } else {
                    scope.launch { drawerState.close() }
                }
            }
            showExportProgress -> exportJob?.cancel()
            else -> onNavigateBack()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            if (showLayersPanel) {
                LayersPanel(
                    modifier = Modifier.fillMaxHeight().width(320.dp),
                    layers = layers,
                    onAddLayer = { pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    onLayerVisibilityChange = { layer -> 
                        val index = layers.indexOfFirst { it.id == layer.id }
                        if (index != -1) { layers[index] = layer.copy(isVisible = !layer.isVisible) }
                    },
                    onLayersRemoved = { ids -> layers.removeAll { ids.contains(it.id) } },
                    onDismiss = { showLayersPanel = false }
                )
            } else if (showExportScreen) {
                ExportScreen(
                    modifier = Modifier.fillMaxHeight().width(320.dp),
                    onDismiss = { showExportScreen = false },
                    onExport = { exportType -> 
                        showExportScreen = false
                        pendingExportType = exportType
                        exportLauncher.launch(null)
                        scope.launch { drawerState.close() }
                    }
                )
            } else if (showFilterPanel) {
                FilterPanel(
                    modifier = Modifier.fillMaxHeight().width(320.dp),
                    onDismiss = { showFilterPanel = false },
                    onGreyscaleChange = { applyGreyscale -> 
                        colorFilter = if (applyGreyscale) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null 
                    }
                )
            } else if (showHistoryPanel) {
                HistoryPanel(
                    modifier = Modifier.fillMaxHeight().width(320.dp),
                    projectDirUri = projectDirUri,
                    onDismiss = { showHistoryPanel = false },
                    onRollback = { snapshot ->
                        scope.launch {
                            val success = ProjectManager.rollback(context, DocumentFile.fromTreeUri(context, projectDirUri)!!, snapshot)
                            if (success) {
                                layers.clear()
                                layers.addAll(ProjectManager.loadLayers(context, DocumentFile.fromTreeUri(context, projectDirUri)!!))
                                showHistoryPanel = false
                                drawerState.close()
                                Toast.makeText(context, "Project Restored", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Rollback Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            } else {
                ToolDrawer(
                    onToolClick = onToolSelected,
                    onFilterClick = { showFilterPanel = true },
                    onAddImageClick = {
                        pickImageLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    onSaveClick = { showSnapshotNameDialog = true },
                    onLayersClick = { showLayersPanel = true },
                    onExportClick = { showExportScreen = true },
                    onHistoryClick = { showHistoryPanel = true },
                    onHomeClick = onNavigateBack,
                    onClose = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                EditorHeader(
                    projectName = projectDirName,
                    resolution = "1024 x 1024",
                    activeLayerName = activeLayerName,
                    lastSaveTime = lastSaveTime,
                    activeToolIcon = activeToolIcon,
                    onTabClick = { scope.launch { if (drawerState.isClosed) drawerState.open() else drawerState.close() } },
                    onQuickSaveClick = performQuickSave // Connected Quick Save button
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    EditorWorkspace(layers = layers, colorFilter = colorFilter)
                }
            }
        }
    }

    if (showSaveConfirmDialog) { SaveConfirmDialog(onDismiss = { showSaveConfirmDialog = false }, onConfirm = { showSaveConfirmDialog = false; scope.launch { ProjectSaveManager.saveProject(context, projectDirUri, layers.toList(), "Return Home Save"); onNavigateBack() } }) }
    showLayerNameDialog?.let { uri -> LayerNameDialog(onDismiss = { showLayerNameDialog = null }, onConfirm = { title -> showLayerNameDialog = null; scope.launch { val newLayer = NewLayerManager.addNewLayer(context, projectDirUri, uri, title); newLayer?.let { layers.add(it) } } }) }
    
    if (showSnapshotNameDialog) {
        SnapshotNameDialog(
            onDismiss = { showSnapshotNameDialog = false },
            onConfirm = { note ->
                showSnapshotNameDialog = false
                scope.launch {
                    ProjectSaveManager.saveProject(context, projectDirUri, layers.toList(), note)
                    lastSaveTime = System.currentTimeMillis()
                    drawerState.close()
                }
            }
        )
    }

    if (showExportProgress) { ExportProgressDialog(progress = exportProgress, onCancel = { exportJob?.cancel() }) }
}
