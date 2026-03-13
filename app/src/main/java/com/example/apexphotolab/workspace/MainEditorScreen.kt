package com.example.apexphotolab.workspace

import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.apexphotolab.*
import com.example.apexphotolab.ui.ToolbarButton
import com.example.apexphotolab.ui.dialogs.LayerNameDialog
import com.example.apexphotolab.ui.dialogs.SaveConfirmDialog
import com.example.apexphotolab.workspace.toolbars.export.data.ExportJobManager
import com.example.apexphotolab.workspace.toolbars.export.ui.ExportProgressDialog
import com.example.apexphotolab.workspace.toolbars.export.ui.ExportScreen
import com.example.apexphotolab.workspace.toolbars.export.data.ExportType
import com.example.apexphotolab.workspace.toolbars.filters.FilterPanel
import com.example.apexphotolab.workspace.toolbars.layers.Layer
import com.example.apexphotolab.workspace.toolbars.layers.LayersPanel
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

    var showSaveConfirmDialog by remember { mutableStateOf(false) }
    var showLayersPanel by remember { mutableStateOf(false) }
    var showLayerNameDialog by remember { mutableStateOf<Uri?>(null) }
    var showExportScreen by remember { mutableStateOf(false) }
    var showFilterPanel by remember { mutableStateOf(false) }
    var colorFilter: ColorFilter? by remember { mutableStateOf(null) }
    var pendingExportType by remember { mutableStateOf<ExportType?>(null) }
    var showExportProgress by remember { mutableStateOf(false) }
    var exportProgress by remember { mutableStateOf(0f) }
    var exportJob by remember { mutableStateOf<Job?>(null) }

    val blurAmount by animateDpAsState(if (showLayersPanel || showExportScreen || showFilterPanel || showExportProgress) 16.dp else 0.dp, label = "blur_amount")

    // --- JOB 1: Load Project (Delegated) ---
    LaunchedEffect(projectDirUri) {
        isLoading = true
        try {
            val result = ProjectLoadManager.loadProject(context, projectDirUri)
            if (result != null) {
                projectDirName = result.projectName
                layers.clear()
                layers.addAll(result.layers)
            } else {
                Toast.makeText(context, "Failed to load project", Toast.LENGTH_SHORT).show()
                onNavigateBack()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading project: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            onNavigateBack()
        } finally {
            isLoading = false
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? -> uri?.let { showLayerNameDialog = it } }
    )

    // --- JOB 2: Run Export (Delegated) ---
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { dirUri: Uri? ->
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
                            onProgress = { progress -> exportProgress = progress }
                        )
                        Toast.makeText(context, "Export Finished", Toast.LENGTH_SHORT).show()
                    } catch (e: CancellationException) {
                        Toast.makeText(context, "Export Cancelled", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Export Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    } finally {
                        showExportProgress = false
                    }
                }
            }
        }
    )

    BackHandler {
        when {
            showExportProgress -> exportJob?.cancel()
            showLayersPanel -> showLayersPanel = false
            showExportScreen -> showExportScreen = false
            showFilterPanel -> showFilterPanel = false
            SettingsManager.getShouldShowSaveConfirmation(context) -> showSaveConfirmDialog = true
            else -> scope.launch { ProjectSaveManager.saveProject(context, projectDirUri, layers.toList()); onNavigateBack() }
        }
    }

    // --- UI DRAWING ---
    Box(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            
            val imageContent = @Composable { imageModifier: Modifier ->
                Box(modifier = imageModifier, contentAlignment = Alignment.Center) {
                     layers.sortedBy { it.zOrder }.forEach { layer ->
                        if (layer.isVisible) {
                            val bitmap by remember(layer.imageUri) {
                                mutableStateOf(try { context.contentResolver.openInputStream(layer.imageUri)?.use { BitmapFactory.decodeStream(it) } } catch (e: Exception) { e.printStackTrace(); null })
                            }
                            bitmap?.let {
                                Image(bitmap = it.asImageBitmap(), contentDescription = layer.title, colorFilter = colorFilter)
                            }
                        }
                    }
                }
            }

            val leftToolbar = @Composable {
                Column(modifier = Modifier.padding(8.dp)) {
                    ToolbarButton(icon = Icons.Default.Tonality, onClick = { showFilterPanel = true })
                    ToolbarButton(icon = Icons.Default.Add, onClick = { pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) })
                    ToolbarButton(icon = Icons.Default.Save, onClick = { scope.launch { ProjectSaveManager.saveProject(context, projectDirUri, layers.toList()) } })
                    ToolbarButton(icon = Icons.Default.Layers, onClick = { showLayersPanel = !showLayersPanel })
                    ToolbarButton(icon = Icons.Default.Person, onClick = { /*TODO*/ })
                    ToolbarButton(icon = Icons.Default.Place, onClick = { /*TODO*/ })
                    ToolbarButton(icon = Icons.Default.Home, onClick = { onNavigateBack() })
                }
            }

            val rightToolbar = @Composable {
                 Column(modifier = Modifier.padding(8.dp)) {
                    ToolbarButton(icon = Icons.Default.FileUpload, onClick = { showExportScreen = true })
                    ToolbarButton(icon = Icons.AutoMirrored.Filled.Send, onClick = { /*TODO*/ })
                    ToolbarButton(icon = Icons.Default.Settings, onClick = { /*TODO*/ })
                    ToolbarButton(icon = Icons.Default.Search, onClick = { /*TODO*/ })
                    ToolbarButton(icon = Icons.Default.Refresh, onClick = { /*TODO*/ })
                    ToolbarButton(icon = Icons.Default.Favorite, onClick = { /*TODO*/ })
                    ToolbarButton(icon = Icons.Default.Info, onClick = { /*TODO*/ })
                }
            }

            Box(modifier = Modifier.fillMaxSize().blur(blurAmount)) {
                if (isLandscape) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        leftToolbar()
                        imageContent(Modifier.weight(1f).fillMaxSize())
                        rightToolbar()
                    }
                } else {
                     Column(modifier = Modifier.fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                           leftToolbar()
                           rightToolbar()
                        }
                        imageContent(Modifier.weight(1f).fillMaxSize())
                    }
                }
            }
            
            if (showLayersPanel) { LayersPanel(modifier = Modifier.align(Alignment.Center), layers = layers, onAddLayer = { pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, onLayerVisibilityChange = { layer -> val index = layers.indexOfFirst { it.id == layer.id }; if (index != -1) { layers[index] = layer.copy(isVisible = !layer.isVisible) } }, onLayersRemoved = { ids -> layers.removeAll { ids.contains(it.id) } }) }
            if (showExportScreen) { ExportScreen(modifier = Modifier.align(Alignment.Center), onDismiss = { showExportScreen = false }, onExport = { exportType -> showExportScreen = false; pendingExportType = exportType; exportLauncher.launch(null) }) }
            if (showFilterPanel) { FilterPanel(modifier = Modifier.align(Alignment.Center), onDismiss = { showFilterPanel = false }, onGreyscaleChange = { applyGreyscale -> colorFilter = if (applyGreyscale) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null }) }
        }
    }

    if (showSaveConfirmDialog) { SaveConfirmDialog(onDismiss = { showSaveConfirmDialog = false }, onConfirm = { showSaveConfirmDialog = false; scope.launch { ProjectSaveManager.saveProject(context, projectDirUri, layers.toList()); onNavigateBack() } }) }
    showLayerNameDialog?.let { uri -> LayerNameDialog(onDismiss = { showLayerNameDialog = null }, onConfirm = { title -> showLayerNameDialog = null; scope.launch { val newLayer = NewLayerManager.addNewLayer(context, projectDirUri, uri, title); newLayer?.let { layers.add(it) } } }) }
    
    if (showExportProgress) {
        ExportProgressDialog(
            progress = exportProgress,
            onCancel = { exportJob?.cancel() }
        )
    }
}
