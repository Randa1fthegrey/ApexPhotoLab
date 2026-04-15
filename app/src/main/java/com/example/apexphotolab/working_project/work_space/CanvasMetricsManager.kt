package com.example.apexphotolab.working_project.work_space

import androidx.compose.ui.unit.IntSize
import com.example.apexphotolab.working_project.tool_panel.layers.Layer

/**
 * Job: Geometry Logic.
 * Responsibility: Calculating canvas aspect ratios and scaling factors for coordinate translation.
 */
object CanvasMetricsManager {

    fun calculateAspectRatio(layers: List<Layer>): Float {
        val baseLayer = layers.find { it.id == "base" }
        return if (baseLayer != null && baseLayer.width > 0 && baseLayer.height > 0) {
            baseLayer.width.toFloat() / baseLayer.height.toFloat()
        } else {
            1f
        }
    }

    fun calculateFitScale(layers: List<Layer>, canvasSize: IntSize): Float {
        val baseLayer = layers.find { it.id == "base" }
        return if (baseLayer != null && canvasSize.width > 0) {
            canvasSize.width.toFloat() / baseLayer.width.toFloat()
        } else {
            1f
        }
    }
}
