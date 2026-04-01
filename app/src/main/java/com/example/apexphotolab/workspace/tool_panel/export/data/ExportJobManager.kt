package com.example.apexphotolab.workspace.tool_panel.export.data

import android.content.Context
import android.net.Uri
import com.example.apexphotolab.workspace.tool_panel.layers.Layer
import java.io.File

/**
 * A manager dedicated to running the export process with safe temp files.
 * Updated: Supports the full Professional Format Suite.
 */
object ExportJobManager {

    suspend fun runExportJob(
        context: Context,
        exportType: ExportType,
        directoryUri: Uri,
        projectName: String,
        layers: List<Layer>,
        isGreyscale: Boolean,
        resolution: String,
        onProgress: (Float) -> Unit
    ) {
        var tempFile: File? = null
        try {
            val isSizeMode = resolution.contains("(Target)")
            
            val (prefix, ext, mime) = when (exportType) {
                ExportType.SVG -> Triple("export", "svg", "image/svg+xml")
                ExportType.PNG -> Triple("export", "png", "image/png")
                ExportType.JPG -> Triple("export", "jpg", "image/jpeg")
                ExportType.WEBP -> Triple("export", "webp", "image/webp")
                ExportType.BMP -> Triple("export", "bmp", "image/bmp")
                ExportType.PSD -> Triple("export", "psd", "image/vnd.adobe.photoshop")
                ExportType.TIFF -> Triple("export", "tiff", "image/tiff")
                ExportType.XCF -> Triple("export", "xcf", "application/x-gimp-xcf")
            }
            tempFile = ExportOrchestrator.createTempFile(context, prefix, ext)

            if (isSizeMode) {
                val targetBytes = parseToBytes(resolution)
                ProjectExporter.exportProjectWithFileSizeLimit(
                    context, layers, Uri.fromFile(tempFile), exportType, isGreyscale, targetBytes, onProgress
                )
            } else {
                val dims = resolution.split("x").map { it.trim().toIntOrNull() ?: 1024 }
                val targetW = dims.getOrElse(0) { 1024 }
                val targetH = dims.getOrElse(1) { 1024 }

                when (exportType) {
                    ExportType.SVG -> ProjectExporter.exportProjectToSvg(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH, onProgress)
                    ExportType.PNG -> ProjectExporter.exportProjectToPng(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH)
                    ExportType.JPG -> ProjectExporter.exportProjectToJpg(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH)
                    ExportType.WEBP -> ProjectExporter.exportProjectToWebp(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH)
                    ExportType.BMP -> ProjectExporter.exportProjectToBmp(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH)
                    ExportType.PSD -> ProjectExporter.exportProjectToPsd(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH)
                    ExportType.TIFF -> ProjectExporter.exportProjectToTiff(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH)
                    ExportType.XCF -> ProjectExporter.exportProjectToXcf(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH)
                }
            }

            val finalFileName = "${projectName}_export.$ext"
            ExportOrchestrator.finalizeExport(context, tempFile, directoryUri, finalFileName, mime)

        } catch (e: Exception) {
            throw e
        } finally {
            tempFile?.delete()
        }
    }

    private fun parseToBytes(resString: String): Long {
        return try {
            val parts = resString.split(" ")
            val value = parts[0].toDoubleOrNull() ?: 500.0
            val unit = parts[1]
            val multiplier = when (unit.uppercase()) {
                "MB" -> 1024 * 1024
                "KB" -> 1024
                else -> 1
            }
            (value * multiplier).toLong()
        } catch (e: Exception) {
            512000L
        }
    }
}
