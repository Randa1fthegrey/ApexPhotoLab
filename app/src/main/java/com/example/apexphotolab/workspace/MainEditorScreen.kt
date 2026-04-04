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
import com.example.apexphotolab.workspace.tool_panel.export.ui.ResolutionCategory
import com.example.apexphotolab.workspace.tool_panel.export.data.ExportType
import com.example.apexphotolab.workspace.tool_panel.filters.FilterPanel
import com.example.apexphotolab.workspace.tool_panel.layers.Layer
import com.example.apexphotolab.workspace.tool_panel.layers.LayersPanel
import com.example.apexphotolab.workspace.tool_panel.layers.ui.ResetCanvasDialog
import com.example.apexphotolab.workspace.tool_panel.save.HistoryPanel
import com.example.apexphotolab.workspace.tool_panel.layers.NewLayerManager
import com.example.apexphotolab.workspace.tool_panel.save.ProjectLoadManager
import com.example.apexphotolab.workspace.tool_panel.save.ProjectManager
import com.example.apexphotolab.workspace.tool_panel.save.ProjectSaveManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Collections
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

    // --- SELECTION STATE ---
    var selectedLayerId by remember { mutableStateOf("base") }

    // --- PERSISTENT EXPORT STATE ---
    var exportResolution by remember { mutableStateOf("1024 x 1024") }
    var exportCategory by remember { mutableStateOf(ResolutionCategory.STANDARD) }
    var widescreenIndex by remember { mutableIntStateOf(1) }
    var standardIndex by remember { mutableIntStateOf(0) }
    var customWidth by remember { mutableStateOf("1024") }
    var customHeight by remember { mutableStateOf("1024") }

    // --- UI STATE ---
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var activeToolIcon by remember { mutableStateOf(Icons.Default.Build) }
    var lastSaveTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val activeLayerName by remember { derivedStateOf { layers.find { it.id == selectedLayerId }?.title ?: "Background" } }
    val isLayerLocked by remember { derivedStateOf { layers.find { it.id == selectedLayerId }?.isLocked ?: false } }
    
    // Panel/Screen States
    var showLayersPanel by remember { mutableStateOf(false) }
    var showExportScreen by remember { mutableStateOf(false) }
    var showFilterPanel by remember { mutableStateOf(false) }
    var showHistoryPanel by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    
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

    // --- REORDER LOGIC ---
    val moveLayer = { layer: Layer, up: Boolean ->
        val index = layers.indexOfFirst { it.id == layer.id }
        if (index != -1) {
            val targetIndex = if (up) index + 1 else index - 1
            if (targetIndex in layers.indices && layers[targetIndex].id != "base") {
                Collections.swap(layers, index, targetIndex)
            }
        }
    }

    // --- RESET LOGIC ---
    val recenterLayers: (String?) -> Unit = { name ->
        if (name == null) {
            layers.indices.forEach { i -> 
                layers[i] = layers[i].copy(xPosition = 0f, yPosition = 0f, rotation = 0f) 
            }
            Toast.makeText(context, "All layers recentered & leveled", Toast.LENGTH_SHORT).show()
        } else {
            val index = layers.indexOfFirst { it.title.equals(name, ignoreCase = true) }
            if (index != -1) {
                layers[index] = layers[index].copy(xPosition = 0f, yPosition = 0f, rotation = 0f)
                Toast.makeText(context, "Layer '$name' recentered & leveled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Layer '$name' not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val reshapeLayers: (String?) -> Unit = { name ->
        if (name == null) {
            layers.indices.forEach { i -> layers[i] = layers[i].copy(scale = 1f) }
            Toast.makeText(context, "All layers reshaped", Toast.LENGTH_SHORT).show()
        } else {
            val index = layers.indexOfFirst { it.title.equals(name, ignoreCase = true) }
            if (index != -1) {
                layers[index] = layers[index].copy(scale = 1f)
                Toast.makeText(context, "Layer '$name' reshaped", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Layer '$name' not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val totalResetLayers: (String?) -> Unit = { name ->
        if (name == null) {
            layers.indices.forEach { i -> 
                layers[i] = layers[i].copy(xPosition = 0f, yPosition = 0f, scale = 1f, rotation = 0f) 
            }
            Toast.makeText(context, "Total reset for all layers", Toast.LENGTH_SHORT).show()
        } else {
            val index = layers.indexOfFirst { it.title.equals(name, ignoreCase = true) }
            if (index != -1) {
                layers[index] = layers[index].copy(xPosition = 0f, yPosition = 0f, scale = 1f, rotation = 0f)
                Toast.makeText(context, "Total reset for '$name'", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Layer '$name' not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- QUICK SAVE LOGIC ---
    val performQuickSave: () -> Unit = {
        scope.launch {
            val history = ProjectManager.getHistory(DocumentFile.fromTreeUri(context, projectDirUri)!!)
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
            // Default "base" to locked if it's a new or existing project
            val initializedLayers = result.layers.map { layer ->
                if (layer.id == "base") layer.copy(isLocked = true) else layer
            }
            layers.addAll(initializedLayers)
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
                    ExportJobManager.runExportJob(
                        context = context,
                        exportType = exportType,
                        directoryUri = directoryUri,
                        projectName = projectDirName,
                        layers = layers,
                        isGreyscale = colorFilter != null,
                        resolution = exportResolution,
                        onProgress = { exportProgress = it }
                    )
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
                    selectedLayerId = selectedLayerId,
                    onLayerSelected = { layer -> selectedLayerId = layer.id },
                    onMoveLayerUp = { layer -> moveLayer(layer, true) },
                    onMoveLayerDown = { layer -> moveLayer(layer, false) },
                    onAddLayer = { pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    onLayerVisibilityChange = { layer -> 
                        val index = layers.indexOfFirst { it.id == layer.id }
                        if (index != -1) { layers[index] = layer.copy(isVisible = !layer.isVisible) }
                    },
                    onLayerLockChange = { layer ->
                        val index = layers.indexOfFirst { it.id == layer.id }
                        if (index != -1) { layers[index] = layer.copy(isLocked = !layer.isLocked) }
                    },
                    onLayersRemoved = { ids -> 
                        layers.removeAll { ids.contains(it.id) }
                        if (ids.contains(selectedLayerId)) selectedLayerId = "base"
                    },
                    onDismiss = { showLayersPanel = false }
                )
            } else if (showExportScreen) {
                ExportScreen(
                    modifier = Modifier.fillMaxHeight().width(320.dp),
                    category = exportCategory,
                    widescreenIndex = widescreenIndex,
                    standardIndex = standardIndex,
                    customW = customWidth,
                    customH = customHeight,
                    onCategoryChange = { exportCategory = it },
                    onWidescreenIndexChange = { widescreenIndex = it },
                    onStandardIndexChange = { standardIndex = it },
                    onCustomWidthChange = { customWidth = it },
                    onCustomHeightChange = { customHeight = it },
                    onDismiss = { showExportScreen = false },
                    onExport = { exportType -> 
                        showExportScreen = false
                        pendingExportType = exportType
                        exportLauncher.launch(null)
                        scope.launch { drawerState.close() }
                    },
                    onResolutionChange = { exportResolution = it }
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
                    onResetClick = { showResetDialog = true },
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
                    resolution = exportResolution,
                    activeLayerName = activeLayerName,
                    isLayerLocked = isLayerLocked,
                    lastSaveTime = lastSaveTime,
                    activeToolIcon = activeToolIcon,
                    onTabClick = { scope.launch { if (drawerState.isClosed) drawerState.open() else drawerState.close() } },
                    onQuickSaveClick = performQuickSave
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    EditorWorkspace(
                        layers = layers, 
                        colorFilter = colorFilter,
                        onLayerTransform = { updatedLayer ->
                            // Only allow transformation if it's the selected layer
                            if (updatedLayer.id == selectedLayerId) {
                                val index = layers.indexOfFirst { it.id == updatedLayer.id }
                                if (index != -1) {
                                    layers[index] = updatedLayer
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showResetDialog) {
        ResetCanvasDialog(
            layers = layers,
            onDismiss = { showResetDialog = false },
            onRecenter = { name: String? ->
                recenterLayers(name)
                scope.launch { drawerState.close() }
            },
            onReshape = { name: String? ->
                reshapeLayers(name)
                scope.launch { drawerState.close() }
            },
            onBoth = { name: String? ->
                totalResetLayers(name)
                scope.launch { drawerState.close() }
            }
        )
    }

    if (showSaveConfirmDialog) { SaveConfirmDialog(onDismiss = { showSaveConfirmDialog = false }, onConfirm = { showSaveConfirmDialog = false; scope.launch { ProjectSaveManager.saveProject(context, projectDirUri, layers.toList(), "Return Home Save"); onNavigateBack() } }) }
    showLayerNameDialog?.let { uri -> LayerNameDialog(onDismiss = { showLayerNameDialog = null }, onConfirm = { title -> showLayerNameDialog = null; scope.launch { val newLayer = NewLayerManager.addNewLayer(context, projectDirUri, uri, title); newLayer?.let { layers.add(it); selectedLayerId = it.id } } }) }
    
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
