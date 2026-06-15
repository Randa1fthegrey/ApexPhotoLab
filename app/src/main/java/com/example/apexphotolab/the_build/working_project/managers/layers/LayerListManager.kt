package com.example.apexphotolab.the_build.working_project.managers.layers

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.apexphotolab.the_build.working_project.util.layers.Layer
import java.util.Collections

/**
 * Job: List Structure Worker.
 * Handles layer reordering, additions, and deletions.
 */
class LayerListManager {
    fun moveLayer(layers: SnapshotStateList<Layer>, layer: Layer, up: Boolean) {
        val index = layers.indexOfFirst { it.id == layer.id }
        if (index != -1) {
            val targetIndex = if (up) index + 1 else index - 1
            if (targetIndex in layers.indices && layers[targetIndex].id != val_util.LAYER_BASE_ID) {
                Collections.swap(layers, index, targetIndex)
            }
        }
    }

    fun addLayer(layers: SnapshotStateList<Layer>, layer: Layer) {
        layers.add(layer)
    }

    fun removeLayers(layers: SnapshotStateList<Layer>, ids: Set<String>) {
        layers.removeAll { ids.contains(it.id) }
    }
}
