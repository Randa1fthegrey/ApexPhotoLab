package com.example.apexphotolab.workspace.tool_panel.export.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.SvgGenerator
import com.example.apexphotolab.workspace.tool_panel.layers.Layer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * A worker that performs the export operation with resizing and file-size targeting support.
 * Updated: Supports specialized WebP variants (Lossy and Lossless).
 */
object ProjectExporter {

    suspend fun exportProjectToPng(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024) = withContext(Dispatchers.IO) {
        val flattenedBitmap = ImageFlattener.flattenLayers(context, layers, applyGreyscale, targetWidth, targetHeight) ?: throw IllegalStateException("Export failed")
        BitmapFileSaver.saveBitmap(context, flattenedBitmap, outputUri, Bitmap.CompressFormat.PNG, 100)
        flattenedBitmap.recycle()
    }

    suspend fun exportProjectToJpg(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024) = withContext(Dispatchers.IO) {
        val flattenedBitmap = ImageFlattener.flattenLayers(context, layers, applyGreyscale, targetWidth, targetHeight) ?: throw IllegalStateException("Export failed")
        BitmapFileSaver.saveBitmap(context, flattenedBitmap, outputUri, Bitmap.CompressFormat.JPEG, 90)
        flattenedBitmap.recycle()
    }

    suspend fun exportProjectToWebp(
        context: Context, 
        layers: List<Layer>, 
        outputUri: Uri, 
        applyGreyscale: Boolean = false, 
        targetWidth: Int = 1024, 
        targetHeight: Int = 1024,
        isLossless: Boolean = false // New parameter
    ) = withContext(Dispatchers.IO) {
        val flattenedBitmap = ImageFlattener.flattenLayers(context, layers, applyGreyscale, targetWidth, targetHeight) ?: throw IllegalStateException("Export failed")
        
        val format = when {
            isLossless -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSLESS else Bitmap.CompressFormat.WEBP
            else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP
        }
        
        BitmapFileSaver.saveBitmap(context, flattenedBitmap, outputUri, format, 90)
        flattenedBitmap.recycle()
    }

    suspend fun exportProjectToBmp(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024) = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
            BmpSaver.saveFlattenedBmp(context, layers, outputStream, targetWidth, targetHeight)
        } ?: throw IllegalStateException("Could not open output stream for BMP")
    }

    suspend fun exportProjectToTiff(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024) = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
            TiffSaver.saveFlattenedTiff(context, layers, outputStream, targetWidth, targetHeight)
        } ?: throw IllegalStateException("Could not open output stream for TIFF")
    }

    suspend fun exportProjectToPsd(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024) = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
            PsdLayeredSaver.saveLayeredPsd(context, layers, outputStream, targetWidth, targetHeight)
        } ?: throw IllegalStateException("Could not open output stream for PSD")
    }

    suspend fun exportProjectToXcf(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024) = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
            XcfSaver.saveLayeredXcf(context, layers, outputStream, targetWidth, targetHeight)
        } ?: throw IllegalStateException("Could not open output stream for XCF")
    }

    suspend fun exportProjectToSvg(context: Context, layers: List<Layer>, outputUri: Uri, applyGreyscale: Boolean = false, targetWidth: Int = 1024, targetHeight: Int = 1024, onProgress: (Float) -> Unit) = withContext(Dispatchers.IO) {
        val flattenedBitmap = ImageFlattener.flattenLayers(context, layers, applyGreyscale, targetWidth, targetHeight) ?: throw IllegalStateException("Export failed")
        ensureActive()
        val svgContent = SvgGenerator.generate(flattenedBitmap, onProgress)
        ensureActive()
        TextFileSaver.saveText(context, svgContent, outputUri)
        flattenedBitmap.recycle()
    }

    suspend fun exportProjectWithFileSizeLimit(context: Context, layers: List<Layer>, outputUri: Uri, type: ExportType, applyGreyscale: Boolean, targetBytes: Long, onProgress: (Float) -> Unit) = withContext(Dispatchers.IO) {
        if (type == ExportType.SVG) {
            exportProjectToSvg(context, layers, outputUri, applyGreyscale, 1024, 1024, onProgress)
            return@withContext
        }
        var currentScale = 1.0f
        val baseBitmap = ImageFlattener.flattenLayers(context, layers, applyGreyscale, 2048, 2048) ?: throw IllegalStateException("Export failed")
        val tempFile = File(context.cacheDir, "size_maximizer.tmp")
        while (currentScale > 0.1f) {
            val w = (baseBitmap.width * currentScale).toInt()
            val h = (baseBitmap.height * currentScale).toInt()
            val scaled = if (currentScale == 1.0f) baseBitmap else Bitmap.createScaledBitmap(baseBitmap, w, h, true)
            var currentQuality = 100
            while (currentQuality >= 30) {
                FileOutputStream(tempFile).use { out -> scaled.compress(Bitmap.CompressFormat.JPEG, currentQuality, out) }
                if (tempFile.length() <= targetBytes) {
                    BitmapFileSaver.saveBitmap(context, scaled, outputUri, Bitmap.CompressFormat.JPEG, currentQuality)
                    if (scaled != baseBitmap) scaled.recycle()
                    baseBitmap.recycle()
                    tempFile.delete()
                    return@withContext
                }
                currentQuality -= 5
            }
            if (scaled != baseBitmap) scaled.recycle()
            currentScale -= 0.1f
            onProgress(1.0f - currentScale)
        }
        BitmapFileSaver.saveBitmap(context, baseBitmap, outputUri, Bitmap.CompressFormat.JPEG, 10)
        baseBitmap.recycle()
        tempFile.delete()
    }
}
