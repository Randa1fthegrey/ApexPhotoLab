package com.example.apexphotolab.workspace.toolbars.export.data

import android.content.Context
import android.net.Uri
import com.example.apexphotolab.workspace.toolbars.export.svg.svg_assembly.SvgGenerator
import com.example.apexphotolab.workspace.toolbars.layers.Layer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/**
 * A simple worker that performs the export operation.
 * It does not handle errors or cancellation itself; it throws exceptions upwards.
 */
object ProjectExporter {
    suspend fun exportProjectToPng(
        context: Context,
        layers: List<Layer>,
        outputUri: Uri,
        applyGreyscale: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val flattenedBitmap = ImageFlattener.flattenLayers(context, layers, applyGreyscale)
            ?: throw IllegalStateException("PNG Export failed: No visible layers.")

        BitmapFileSaver.saveBitmap(context, flattenedBitmap, outputUri)
        flattenedBitmap.recycle()
    }

    suspend fun exportProjectToSvg(
        context: Context,
        layers: List<Layer>,
        outputUri: Uri,
        applyGreyscale: Boolean = false,
        onProgress: (Float) -> Unit
    ) = withContext(Dispatchers.IO) {
        val flattenedBitmap = ImageFlattener.flattenLayers(context, layers, applyGreyscale)
            ?: throw IllegalStateException("SVG Export failed: No visible layers.")

        ensureActive() // Cooperate with cancellation before heavy work
        val svgContent = SvgGenerator.generate(flattenedBitmap, onProgress)
        ensureActive() // Cooperate with cancellation before writing to disk

        TextFileSaver.saveText(context, svgContent, outputUri)
        flattenedBitmap.recycle()
    }
}