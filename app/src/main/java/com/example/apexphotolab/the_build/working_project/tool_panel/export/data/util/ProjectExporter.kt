package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.managers.BitmapExportManager
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers.BitmapFileSaver
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers.BmpSaver
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers.PsdLayeredSaver
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers.TextFileSaver
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers.TiffSaver
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers.XcfSaver
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.SvgGenerator
import com.example.apexphotolab.the_build.working_project.util.layers.Layer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/**
 * Job: Logic Foreman (Route Manager).
 * Responsibility: Orchestrating the export by routing to the correct specialist managers or savers.
 */
object ProjectExporter {

    suspend fun exportProjectToPng(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024) {
        BitmapExportManager.flattenAndProcess(context, layers, applyGreyscale, targetWidth, targetHeight) { bitmap ->
            BitmapFileSaver.saveBitmap(context, bitmap, outputUri, Bitmap.CompressFormat.PNG, 100)
        }
    }

    suspend fun exportProjectToJpg(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024) {
        BitmapExportManager.flattenAndProcess(context, layers, applyGreyscale, targetWidth, targetHeight) { bitmap ->
            BitmapFileSaver.saveBitmap(context, bitmap, outputUri, Bitmap.CompressFormat.JPEG, 90)
        }
    }

    suspend fun exportProjectToWebp(
        context: Context,
        layers: List<Layer>,
        outputUri: Uri,
        applyGreyscale: Boolean = false,
        targetWidth: Int = 1024,
        targetHeight: Int = 1024,
        isLossless: Boolean = false
    ) {
        BitmapExportManager.flattenAndProcess(context, layers, applyGreyscale, targetWidth, targetHeight) { bitmap ->
            val format = when {
                isLossless -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSLESS else Bitmap.CompressFormat.WEBP
                else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP
            }
            BitmapFileSaver.saveBitmap(context, bitmap, outputUri, format, 90)
        }
    }

    suspend fun exportProjectToSvg(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024, onProgress: (Float) -> Unit) {
        BitmapExportManager.flattenAndProcess(context, layers, applyGreyscale, targetWidth, targetHeight) { bitmap ->
            currentCoroutineContext().ensureActive()
            val svgContent = SvgGenerator.generate(bitmap, onProgress)
            currentCoroutineContext().ensureActive()
            TextFileSaver.saveText(context, svgContent, outputUri)
        }
    }

    suspend fun exportProjectToBmp(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024) =
        withContext(Dispatchers.IO) {
            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                BmpSaver.saveFlattenedBmp(context, layers, outputStream, targetWidth, targetHeight)
            } ?: throw IllegalStateException("Could not open output stream for BMP")
        }

    suspend fun exportProjectToTiff(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024) =
        withContext(Dispatchers.IO) {
            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                TiffSaver.saveFlattenedTiff(
                    context,
                    layers,
                    outputStream,
                    targetWidth,
                    targetHeight
                )
            } ?: throw IllegalStateException("Could not open output stream for TIFF")
        }

    suspend fun exportProjectToPsd(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024) =
        withContext(Dispatchers.IO) {
            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                PsdLayeredSaver.saveLayeredPsd(
                    context,
                    layers,
                    outputStream,
                    targetWidth,
                    targetHeight
                )
            } ?: throw IllegalStateException("Could not open output stream for PSD")
        }

    suspend fun exportProjectToXcf(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024) =
        withContext(Dispatchers.IO) {
            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                XcfSaver.saveLayeredXcf(context, layers, outputStream, targetWidth, targetHeight)
            } ?: throw IllegalStateException("Could not open output stream for XCF")
        }
}
