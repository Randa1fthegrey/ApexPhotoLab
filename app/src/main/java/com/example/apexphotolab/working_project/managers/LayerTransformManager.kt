package com.example.apexphotolab.working_project.managers

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import com.example.apexphotolab.working_project.workspace.WorkspaceFilterModel
import com.example.apexphotolab.working_project.tool_panel.layers.Layer

/**
 * Job: Property Logic Worker.
 * Handles resetting layer transforms and toggling workspace-level properties.
 */
class LayerTransformManager {

    fun toggleGreyscale(filterModel: WorkspaceFilterModel, applyGreyscale: Boolean) {
        filterModel.colorFilter = if (applyGreyscale) {
            ColorFilter.Companion.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
        } else {
            null
        }
    }

    fun toggleLayerLock(layers: SnapshotStateList<Layer>, layerId: String) {
        val index = layers.indexOfFirst { it.id == layerId }
        if (index != -1) {
            layers[index] = layers[index].copy(isLocked = !layers[index].isLocked)
        }
    }

    fun toggleLayerVisibility(layers: SnapshotStateList<Layer>, layerId: String) {
        val index = layers.indexOfFirst { it.id == layerId }
        if (index != -1) {
            layers[index] = layers[index].copy(isVisible = !layers[index].isVisible)
        }
    }

    fun updateLayerVisibility(layers: SnapshotStateList<Layer>, layerId: String, isVisible: Boolean) {
        val index = layers.indexOfFirst { it.id == layerId }
        if (index != -1) {
            layers[index] = layers[index].copy(isVisible = isVisible)
        }
    }

    fun updateLayer(layers: SnapshotStateList<Layer>, layer: Layer) {
        val index = layers.indexOfFirst { it.id == layer.id }
        if (index != -1) {
            layers[index] = layer
        }
    }

    /**
     * Logic for calculating new layer properties based on gesture input.
     */
    fun applyMoveTransform(
        layers: List<Layer>,
        selectedLayerId: String,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        onUpdate: (Layer) -> Unit
    ) {
        val layer = layers.find { it.id == selectedLayerId }
        layer?.let {
            if (it.isLocked) return
            val updatedLayer = it.copy(
                xPosition = it.xPosition + pan.x,
                yPosition = it.yPosition + pan.y,
                scale = (it.scale * zoom).coerceIn(0.05f, 20f),
                rotation = it.rotation + rotation
            )
            onUpdate(updatedLayer)
        }
    }

    fun recenterLayers(layers: SnapshotStateList<Layer>, name: String?) {
        if (name == null) {
            layers.indices.forEach { i ->
                layers[i] = layers[i].copy(xPosition = 0f, yPosition = 0f, rotation = 0f)
            }
        } else {
            val index = layers.indexOfFirst { it.title.equals(name, ignoreCase = true) }
            if (index != -1) {
                layers[index] = layers[index].copy(xPosition = 0f, yPosition = 0f, rotation = 0f)
            }
        }
    }

    fun reshapeLayers(layers: SnapshotStateList<Layer>, name: String?) {
        if (name == null) {
            layers.indices.forEach { i -> layers[i] = layers[i].copy(scale = 1f) }
        } else {
            val index = layers.indexOfFirst { it.title.equals(name, ignoreCase = true) }
            if (index != -1) {
                layers[index] = layers[index].copy(scale = 1f)
            }
        }
    }

    fun totalResetLayers(layers: SnapshotStateList<Layer>, name: String?) {
        if (name == null) {
            layers.indices.forEach { i ->
                layers[i] = layers[i].copy(xPosition = 0f, yPosition = 0f, scale = 1f, rotation = 0f)
            }
        } else {
            val index = layers.indexOfFirst { it.title.equals(name, ignoreCase = true) }
            if (index != -1) {
                layers[index] = layers[index].copy(xPosition = 0f, yPosition = 0f, scale = 1f, rotation = 0f)
            }
        }
    }
}