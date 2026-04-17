package com.example.apexphotolab.working_project.editor

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.apexphotolab.working_project.header.EditorHeader
import com.example.apexphotolab.working_project.tool_panel.ToolDrawer
import com.example.apexphotolab.working_project.tool_panel.export.ui.ExportProgressDialog
import com.example.apexphotolab.working_project.tool_panel.export.ui.ExportScreen
import com.example.apexphotolab.working_project.tool_panel.filters.FilterPanel
import com.example.apexphotolab.working_project.tool_panel.layers.LayersPanel
import com.example.apexphotolab.working_project.tool_panel.layers.dialogs.LayerNameDialog
import com.example.apexphotolab.working_project.tool_panel.layers.ui.ResetCanvasDialog
import com.example.apexphotolab.working_project.tool_panel.save.HistoryPanel
import com.example.apexphotolab.working_project.tool_panel.save.dialogs.SaveConfirmDialog
import com.example.apexphotolab.working_project.tool_panel.save.dialogs.SnapshotNameDialog
import com.example.apexphotolab.working_project.workspace.EditorWorkspace
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.apexphotolab.working_project.workspace.icon

@Composable
fun MainEditorScreen(
    modifier: Modifier = Modifier,
    projectDirUri: Uri,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state = rememberEditorState()

    // --- LOAD LOGIC ---
    LaunchedEffect(projectDirUri) {
        val success = state.loadProject(context, projectDirUri)
        if (!success) onNavigateBack()
    }

    // --- ACTIVITY LAUNCHERS ---
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> 
        uri?.let { state.showLayerNameDialog = it } 
    }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { dirUri ->
        dirUri?.let { directoryUri -> state.startExport(context, directoryUri, scope) }
    }

    // --- BACK HANDLING ---
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    BackHandler {
        when {
            drawerState.isOpen -> state.closeAllPanels(scope, drawerState)
            state.showExportProgress -> state.exportJob?.cancel()
            else -> onNavigateBack()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxHeight().width(320.dp)) {
                    when {
                        state.showLayersPanel -> {
                            LayersPanel(
                                modifier = Modifier.fillMaxSize(),
                                layers = state.layers,
                                selectedLayerId = state.selectedLayerId,
                                onLayerSelected = { state.selectedLayerId = it.id },
                                onMoveLayerUp = { state.moveLayer(it, true) },
                                onMoveLayerDown = { state.moveLayer(it, false) },
                                onAddLayer = { pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                onLayerVisibilityChange = { state.updateLayerVisibility(it) },
                                onLayerLockChange = { state.updateLayerLock(it) },
                                onLayersRemoved = { state.removeLayers(it) },
                                onDismiss = { state.showLayersPanel = false }
                            )
                        }
                        state.showExportScreen -> {
                            ExportScreen(
                                modifier = Modifier.fillMaxSize(),
                                category = state.exportCategory,
                                widescreenIndex = state.widescreenIndex,
                                standardIndex = state.standardIndex,
                                customW = state.customWidth,
                                customH = state.customHeight,
                                onCategoryChange = { state.updateExportCategory(it) },
                                onWidescreenIndexChange = { state.updateWidescreenIndex(it) },
                                onStandardIndexChange = { state.updateStandardIndex(it) },
                                onCustomWidthChange = { state.updateCustomWidth(it) },
                                onCustomHeightChange = { state.updateCustomHeight(it) },
                                onDismiss = { state.showExportScreen = false },
                                onExport = { exportType -> 
                                    state.showExportScreen = false
                                    state.pendingExportType = exportType
                                    exportLauncher.launch(null)
                                    scope.launch { drawerState.close() }
                                },
                                onResolutionChange = { state.updateExportResolution(it) }
                            )
                        }
                        state.showFilterPanel -> {
                            FilterPanel(
                                modifier = Modifier.fillMaxSize(),
                                availableFilters = listOf(
                                    com.example.apexphotolab.working_project.tool_panel.filters.GreyscaleFilter
                                ),
                                onFilterSelected = { filter -> state.updateActiveFilter(filter) },
                                onDismiss = { state.showFilterPanel = false }
                            )
                        }
                        state.showHistoryPanel -> {
                            HistoryPanel(
                                modifier = Modifier.fillMaxSize(),
                                projectDirUri = projectDirUri,
                                onDismiss = { state.showHistoryPanel = false },
                                onRollback = { snapshot -> state.rollbackProject(context, projectDirUri, snapshot, scope, drawerState) }
                            )
                        }
                        else -> {
                            ToolDrawer(
                                activeTool = state.activeTool,
                                onToolSelected = { state.activeTool = it },
                                onFilterClick = { state.showFilterPanel = true },
                                onAddImageClick = {
                                    pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                onSaveClick = { state.showSnapshotNameDialog = true },
                                onLayersClick = { state.showLayersPanel = true },
                                onExportClick = { state.showExportScreen = true },
                                onHistoryClick = { state.showHistoryPanel = true },
                                onResetClick = { state.showResetDialog = true },
                                onHomeClick = onNavigateBack,
                                brushSize = state.brushSize,
                                onBrushSizeChange = { state.brushSize = it },
                                eraserMode = state.eraserMode,
                                onEraserModeChange = { state.eraserMode = it },
                                onClose = { scope.launch { drawerState.close() } }
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                EditorHeader(
                    projectName = state.projectDirName,
                    resolution = state.exportResolution,
                    currentResolution = state.exportResolution,
                    activeLayerName = state.activeLayerName,
                    isLayerLocked = state.isLayerLocked,
                    lastSaveTime = state.lastSaveTime,
                    activeToolIcon = state.activeTool.icon,
                    onTabClick = { scope.launch { if (drawerState.isClosed) drawerState.open() else drawerState.close() } },
                    onLockToggle = { state.toggleLayerLock() },
                    onQuickSaveClick = { state.quickSaveProject(context, projectDirUri, scope) }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    EditorWorkspace(
                        layers = state.layers, 
                        selectedLayerId = state.selectedLayerId,
                        activeTool = state.activeTool,
                        brushSize = state.brushSize,
                        eraserMode = state.eraserMode,
                        colorFilter = state.colorFilter,
                        onMoveGesture = { pan, zoom, rotation -> state.handleMoveGesture(pan, zoom, rotation) },
                        onLayerEdit = { _, _, _, _ -> },
                        onLayerAction = { _ -> }
                    )
                }
            }
        }
    }

    if (state.showResetDialog) {
        ResetCanvasDialog(
            layers = state.layers,
            onDismiss = { state.showResetDialog = false },
            onRecenter = { name ->
                state.recenterLayers(context, name)
                state.closeAllPanels(scope, drawerState)
            },
            onReshape = { name ->
                state.reshapeLayers(context, name)
                state.closeAllPanels(scope, drawerState)
            },
            onBoth = { name ->
                state.totalResetLayers(context, name)
                state.closeAllPanels(scope, drawerState)
            }
        )
    }

    if (state.showSaveConfirmDialog) { 
        SaveConfirmDialog(
            onDismiss = { state.showSaveConfirmDialog = false }, 
            onConfirm = { 
                state.showSaveConfirmDialog = false
                state.quickSaveProject(context, projectDirUri, scope)
                onNavigateBack() 
            }
        ) 
    }

    state.showLayerNameDialog?.let { uri -> 
        LayerNameDialog(
            onDismiss = { state.showLayerNameDialog = null }, 
            onConfirm = { title -> 
                state.showLayerNameDialog = null
                state.addLayerFromUri(context, projectDirUri, uri, title, scope)
            }
        ) 
    }
    
    if (state.showSnapshotNameDialog) {
        SnapshotNameDialog(
            onDismiss = { state.showSnapshotNameDialog = false },
            onConfirm = { note ->
                state.showSnapshotNameDialog = false
                state.saveSnapshot(context, projectDirUri, note, scope, drawerState)
            }
        )
    }

    if (state.showExportProgress) { 
        ExportProgressDialog(progress = state.exportProgress, onCancel = { state.exportJob?.cancel() })
    }
}
