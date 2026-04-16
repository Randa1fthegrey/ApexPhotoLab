package com.example.apexphotolab.working_project.editor

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.documentfile.provider.DocumentFile
import com.example.apexphotolab.working_project.workspace.WorkspaceFilterModel
import com.example.apexphotolab.working_project.workspace.WorkspaceTool
import com.example.apexphotolab.working_project.managers.ExportManager
import com.example.apexphotolab.working_project.managers.LayerListManager
import com.example.apexphotolab.working_project.managers.LayerTransformManager
import com.example.apexphotolab.working_project.managers.PanelManager
import com.example.apexphotolab.working_project.managers.ProjectPersistenceManager
import com.example.apexphotolab.working_project.managers.ToolSettingsManager
import com.example.apexphotolab.working_project.managers.WorkspacePanel
import com.example.apexphotolab.working_project.tool_panel.eraser.EraserMode
import com.example.apexphotolab.working_project.tool_panel.export.data.ExportJobManager
import com.example.apexphotolab.working_project.tool_panel.export.data.ExportType
import com.example.apexphotolab.working_project.tool_panel.export.ui.ResolutionCategory
import com.example.apexphotolab.working_project.tool_panel.layers.Layer
import com.example.apexphotolab.working_project.tool_panel.layers.NewLayerManager
import com.example.apexphotolab.working_project.tool_panel.save.ProjectSaveManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Job: Logic Orchestrator (The "Brain").
 */
@Stable
class EditorState(
    private val model: EditorModel,
    private val filterModel: WorkspaceFilterModel,
    private val persistenceManager: ProjectPersistenceManager,
    private val listManager: LayerListManager,
    private val transformManager: LayerTransformManager,
    private val panelManager: PanelManager,
    private val toolManager: ToolSettingsManager,
    private val exportManager: ExportManager
) {
    // --- DERIVED UI STATE ---
    val layers: SnapshotStateList<Layer> get() = model.layers
    val projectDirName: String get() = persistenceManager.projectDirName
    val isLoading: Boolean get() = persistenceManager.isLoading
    val lastSaveTime: Long get() = persistenceManager.lastSaveTime
    val colorFilter: ColorFilter? get() = filterModel.colorFilter

    // --- TOOL DELEGATION ---
    var activeTool: WorkspaceTool
        get() = toolManager.activeTool
        set(value) { toolManager.activeTool = value }

    var brushSize: Float
        get() = toolManager.brushSize
        set(value) { toolManager.brushSize = value }

    var eraserMode: EraserMode
        get() = toolManager.eraserMode
        set(value) { toolManager.eraserMode = value }

    var selectedLayerId: String
        get() = toolManager.selectedLayerId
        set(value) { toolManager.selectedLayerId = value }

    val currentLayer: Layer?
        get() = layers.find { it.id == selectedLayerId }

    val activeLayerName: String
        get() = currentLayer?.title ?: "Background"

    val isLayerLocked: Boolean
        get() = currentLayer?.isLocked ?: false

    // --- PANEL DELEGATION ---
    var showResetDialog: Boolean
        get() = panelManager.showResetDialog
        set(value) { panelManager.showResetDialog = value }

    var showSaveConfirmDialog: Boolean
        get() = panelManager.showSaveConfirmDialog
        set(value) { panelManager.showSaveConfirmDialog = value }

    var showLayerNameDialog: Uri?
        get() = panelManager.showLayerNameDialog
        set(value) { panelManager.showLayerNameDialog = value }

    var showSnapshotNameDialog: Boolean
        get() = panelManager.showSnapshotNameDialog
        set(value) { panelManager.showSnapshotNameDialog = value }

    var showLayersPanel: Boolean
        get() = panelManager.showLayersPanel
        set(value) { panelManager.showLayersPanel = value }

    var showExportScreen: Boolean
        get() = panelManager.showExportScreen
        set(value) { panelManager.showExportScreen = value }

    var showFilterPanel: Boolean
        get() = panelManager.showFilterPanel
        set(value) { panelManager.showFilterPanel = value }

    var showHistoryPanel: Boolean
        get() = panelManager.showHistoryPanel
        set(value) { panelManager.showHistoryPanel = value }

    // --- EXPORT DELEGATION ---
    var showExportProgress: Boolean
        get() = exportManager.showExportProgress
        set(value) { exportManager.showExportProgress = value }

    var exportProgress: Float
        get() = exportManager.exportProgress
        set(value) { exportManager.exportProgress = value }

    var exportJob: Job?
        get() = exportManager.exportJob
        set(value) { exportManager.exportJob = value }

    var pendingExportType: ExportType?
        get() = exportManager.pendingExportType
        set(value) { exportManager.pendingExportType = value }

    var exportResolution: String 
        get() = exportManager.exportResolution
        set(value) { exportManager.exportResolution = value }

    var exportCategory: ResolutionCategory 
        get() = exportManager.exportCategory
        set(value) { exportManager.exportCategory = value }

    var widescreenIndex: Int 
        get() = exportManager.widescreenIndex
        set(value) { exportManager.widescreenIndex = value }

    var standardIndex: Int 
        get() = exportManager.standardIndex
        set(value) { exportManager.standardIndex = value }

    var customWidth: String 
        get() = exportManager.customWidth
        set(value) { exportManager.customWidth = value }

    var customHeight: String 
        get() = exportManager.customHeight
        set(value) { exportManager.customHeight = value }

    fun updateExportResolution(res: String) { exportResolution = res }
    fun updateExportCategory(cat: ResolutionCategory) { exportCategory = cat }
    fun updateWidescreenIndex(idx: Int) { widescreenIndex = idx }
    fun updateStandardIndex(idx: Int) { standardIndex = idx }
    fun updateCustomWidth(w: String) { customWidth = w }
    fun updateCustomHeight(h: String) { customHeight = h }

    fun closeAllPanels(scope: CoroutineScope, drawerState: DrawerState) {
        panelManager.clearPanelStates()
        scope.launch { drawerState.close() }
    }

    fun openPanel(panel: WorkspacePanel, scope: CoroutineScope, drawerState: DrawerState) {
        panelManager.openPanel(panel)
        scope.launch { drawerState.open() }
    }

    // --- ORCHESTRATION ---

    fun startExport(context: Context, directoryUri: Uri, scope: CoroutineScope) {
        val exportType = pendingExportType ?: return
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
                Toast.makeText(context, "Export Failed", Toast.LENGTH_SHORT).show()
            } finally {
                showExportProgress = false
            }
        }
    }

    fun addLayerFromUri(context: Context, projectUri: Uri, imageUri: Uri, title: String, scope: CoroutineScope) {
        scope.launch {
            val newLayer = NewLayerManager.addNewLayer(context, projectUri, imageUri, title)
            if (newLayer != null) {
                addLayer(newLayer)
            } else {
                Toast.makeText(context, "Failed to add layer", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveSnapshot(context: Context, projectUri: Uri, note: String, scope: CoroutineScope, drawerState: DrawerState) {
        scope.launch {
            ProjectSaveManager.saveProject(context, projectUri, layers.toList(), note)
            updateSaveTime()
            closeAllPanels(scope, drawerState)
            Toast.makeText(context, "Snapshot Saved", Toast.LENGTH_SHORT).show()
        }
    }

    fun rollbackProject(context: Context, projectUri: Uri, snapshot: DocumentFile, scope: CoroutineScope, drawerState: DrawerState) {
        scope.launch {
            val success = rollback(context, projectUri, snapshot)
            if (success) {
                closeAllPanels(scope, drawerState)
                Toast.makeText(context, "Project Restored", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Rollback Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun quickSaveProject(context: Context, projectUri: Uri, scope: CoroutineScope) {
        scope.launch {
            val success = quickSave(context, projectUri)
            if (success) {
                Toast.makeText(context, "Saved project!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Save Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun recenterLayers(context: Context, name: String?) {
        transformManager.recenterLayers(layers, name)
        val msg = if (name == null) "All layers recentered & leveled" else "Layer '$name' recentered & leveled"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun reshapeLayers(context: Context, name: String?) {
        transformManager.reshapeLayers(layers, name)
        val msg = if (name == null) "All layers reshaped" else "Layer '$name' reshaped"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun totalResetLayers(context: Context, name: String?) {
        transformManager.totalResetLayers(layers, name)
        val msg = if (name == null) "Total reset for all layers" else "Total reset for '$name'"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun toggleGreyscale(applyGreyscale: Boolean) {
        transformManager.toggleGreyscale(filterModel, applyGreyscale)
    }

    fun toggleLayerLock() {
        transformManager.toggleLayerLock(layers, selectedLayerId)
    }

    fun handleMoveGesture(pan: Offset, zoom: Float, rotation: Float) {
        transformManager.applyMoveTransform(
            layers = layers,
            selectedLayerId = selectedLayerId,
            pan = pan,
            zoom = zoom,
            rotation = rotation,
            onUpdate = { updateLayer(it) }
        )
    }

    fun updateLayerVisibility(layer: Layer) {
        transformManager.toggleLayerVisibility(layers, layer.id)
    }

    fun updateLayer(layer: Layer) {
        transformManager.updateLayer(layers, layer)
    }

    fun updateLayerLock(layer: Layer) {
        transformManager.updateLayer(layers, layer)
    }

    fun removeLayers(ids: Set<String>) {
        listManager.removeLayers(layers, ids)
        if (ids.contains(selectedLayerId)) {
            selectedLayerId = "base"
        }
    }

    fun addLayer(layer: Layer) {
        listManager.addLayer(layers, layer)
        selectedLayerId = layer.id
    }

    fun moveLayer(layer: Layer, up: Boolean) {
        listManager.moveLayer(layers, layer, up)
    }

    suspend fun loadProject(context: Context, uri: Uri) =
        persistenceManager.loadProject(context, uri, layers)

    suspend fun quickSave(context: Context, uri: Uri) =
        persistenceManager.performQuickSave(context, uri, layers.toList())

    suspend fun rollback(context: Context, uri: Uri, snapshot: DocumentFile) =
        persistenceManager.rollback(context, uri, snapshot, layers)

    fun updateSaveTime() {
        persistenceManager.updateSaveTime()
    }
}

@Composable
fun rememberEditorState(
    model: EditorModel = remember { EditorModel() },
    filterModel: WorkspaceFilterModel = remember { WorkspaceFilterModel() },
    persistenceManager: ProjectPersistenceManager = remember { ProjectPersistenceManager() },
    listManager: LayerListManager = remember { LayerListManager() },
    transformManager: LayerTransformManager = remember { LayerTransformManager() },
    panelManager: PanelManager = remember { PanelManager() },
    toolManager: ToolSettingsManager = remember { ToolSettingsManager() },
    exportManager: ExportManager = remember { ExportManager() }
): EditorState {
    return remember(model, filterModel, persistenceManager, listManager, transformManager, panelManager, toolManager, exportManager) {
        EditorState(model, filterModel, persistenceManager, listManager, transformManager, panelManager, toolManager, exportManager)
    }
}
