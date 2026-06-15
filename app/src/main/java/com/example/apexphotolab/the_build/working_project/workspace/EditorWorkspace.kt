package com.example.apexphotolab.the_build.working_project.workspace

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.apexphotolab.the_build.working_project.managers.CanvasMetricsManager
import com.example.apexphotolab.the_build.working_project.managers.PrecisionAction
import com.example.apexphotolab.the_build.working_project.managers.WorkspaceInteractionManager
import com.example.apexphotolab.the_build.working_project.tool_panel.brush_logic.BrushOverlay
import com.example.apexphotolab.the_build.working_project.tool_panel.eraser.EraserMode
import com.example.apexphotolab.the_build.working_project.util.layers.Layer

/**
 * Job: The "Stage" (UI Composition).
 * Purified: Logic-Blind view that delegates interaction and metrics to specialized workers.
 */
@Composable
fun EditorWorkspace(
    modifier: Modifier = Modifier,
    layers: List<Layer>,
    selectedLayerId: String,
    activeTool: WorkspaceTool,
    brushSize: Float,
    eraserMode: EraserMode,
    colorFilter: ColorFilter?,
    onMoveGesture: (Offset, Float, Float) -> Unit,
    onLayerEdit: (String, Offset, Offset?, Float) -> Unit,
    onLayerAction: (String) -> Unit
) {
    val interactionManager = remember { WorkspaceInteractionManager() }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val canvasAspectRatio = remember(layers) { 
        CanvasMetricsManager.calculateAspectRatio(layers)
    }
    
    val fitScale = remember(layers, canvasSize) { 
        CanvasMetricsManager.calculateFitScale(layers, canvasSize)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(selectedLayerId, activeTool, eraserMode) {
                when (activeTool) {
                    WorkspaceTool.MOVE -> {
                        detectTransformGestures { _, pan, zoom, rotation ->
                            onMoveGesture(pan, zoom, rotation)
                        }
                    }
                    WorkspaceTool.ERASER -> {
                        if (eraserMode == EraserMode.FREEFORM) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    interactionManager.onFreeformStart(offset) { pos, prev ->
                                        onLayerEdit(selectedLayerId, pos, prev, fitScale)
                                    }
                                },
                                onDrag = { change, _ ->
                                    interactionManager.onFreeformDrag(change) { pos, prev ->
                                        onLayerEdit(selectedLayerId, pos, prev, fitScale)
                                    }
                                },
                                onDragEnd = { interactionManager.updateBrushPosition(null) },
                                onDragCancel = { interactionManager.updateBrushPosition(null) }
                            )
                        } else {
                            awaitEachGesture {
                                val down = awaitFirstDown()
                                val action = interactionManager.handlePrecisionDown(down.position, brushSize)
                                
                                if (action == PrecisionAction.MOVE) {
                                    do {
                                        val event = awaitPointerEvent()
                                        val pointer = event.changes.find { it.id == down.id }
                                        pointer?.let {
                                            if (it.pressed) {
                                                val delta = it.position - it.previousPosition
                                                interactionManager.handlePrecisionMove(delta) { pos, prev ->
                                                    onLayerEdit(selectedLayerId, pos, prev, fitScale)
                                                }
                                                it.consume()
                                            }
                                        }
                                    } while (pointer != null && pointer.pressed)
                                }
                            }
                        }
                    }
                    WorkspaceTool.REMOVE_BG -> {
                        interactionManager.updateBrushPosition(null)
                        detectTapGestures {
                            onLayerAction(selectedLayerId)
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        WorkspaceBackground(modifier = Modifier.fillMaxSize()) {
            // Background is purely decorative
        }

        Box(
            modifier = Modifier
                .padding(val_util.CANVAS_PADDING)
                .aspectRatio(canvasAspectRatio, matchHeightConstraintsFirst = true)
                .onGloballyPositioned { canvasSize = it.size },
            contentAlignment = Alignment.Center
        ) {
            WorkspaceOrchestrator(
                layers = layers,
                colorFilter = colorFilter
            )
        }

        if (activeTool == WorkspaceTool.ERASER) {
            BrushOverlay(
                brushPosition = interactionManager.brushPosition,
                brushSize = brushSize,
                isPrecision = eraserMode == EraserMode.PRECISION
            )
        }
    }
}

