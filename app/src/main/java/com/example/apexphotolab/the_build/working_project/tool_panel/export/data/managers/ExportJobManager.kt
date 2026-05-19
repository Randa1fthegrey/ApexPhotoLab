package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.managers

import android.content.Context
import android.net.Uri
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.util.ExportType
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.util.ProjectExporter
import com.example.apexphotolab.the_build.working_project.util.layers.Layer
import java.io.File

/**
 * Job: Export Orchestrator.
 * Responsibility: Running the full export job lifecycle, from format selection to file finalization.
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
            val (prefix, ext, mime) = when (exportType) {
                ExportType.SVG -> Triple("export", "svg", "image/svg+xml")
                ExportType.PNG -> Triple("export", "png", "image/png")
                ExportType.JPG -> Triple("export", "jpg", "image/jpeg")
                ExportType.WEBP_LOSSY, ExportType.WEBP_LOSSLESS -> Triple("export", "webp", "image/webp")
                ExportType.BMP -> Triple("export", "bmp", "image/bmp")
                ExportType.PSD -> Triple("export", "psd", "image/vnd.adobe.photoshop")
                ExportType.TIFF -> Triple("export", "tiff", "image/tiff")
                ExportType.XCF -> Triple("export", "xcf", "application/x-gimp-xcf")
            }
            tempFile = ExportFileManager.createTempFile(context, prefix, ext)

            val dims = resolution.split("x").map { it.trim().toIntOrNull() ?: 1024 }
            val targetW = dims.getOrElse(0) { 1024 }
            val targetH = dims.getOrElse(1) { 1024 }

            when (exportType) {
                ExportType.SVG -> ProjectExporter.exportProjectToSvg(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH, onProgress)
                ExportType.PNG -> ProjectExporter.exportProjectToPng(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH)
                ExportType.JPG -> ProjectExporter.exportProjectToJpg(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH)
                ExportType.WEBP_LOSSY -> ProjectExporter.exportProjectToWebp(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH, isLossless = false)
                ExportType.WEBP_LOSSLESS -> ProjectExporter.exportProjectToWebp(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH, isLossless = true)
                ExportType.BMP -> ProjectExporter.exportProjectToBmp(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH)
                ExportType.PSD -> ProjectExporter.exportProjectToPsd(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH)
                ExportType.TIFF -> ProjectExporter.exportProjectToTiff(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH)
                ExportType.XCF -> ProjectExporter.exportProjectToXcf(context, layers, Uri.fromFile(tempFile), isGreyscale, targetW, targetH)
            }

            // DYNAMIC FILENAME LOGIC: Append suffix for specialized WebP formats
            val suffix = when (exportType) {
                ExportType.WEBP_LOSSY -> "_Lossy"
                ExportType.WEBP_LOSSLESS -> "_Lossless"
                else -> "_export"
            }

            val finalFileName = "${projectName}${suffix}.$ext"
            ExportFileManager.finalizeExport(context, tempFile, directoryUri, finalFileName, mime)

        } finally {
            tempFile?.delete()
        }
    }
}