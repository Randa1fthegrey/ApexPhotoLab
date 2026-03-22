package com.example.apexphotolab.workspace.tool_panel.export.data

import android.content.Context
import android.net.Uri
import com.example.apexphotolab.workspace.tool_panel.layers.Layer
import java.io.File

/**
 * A manager dedicated to the single, complex job of running the export process
 * using a safe, transactional workflow.
 */
object ExportJobManager {

    suspend fun runExportJob(
        context: Context,
        exportType: ExportType,
        directoryUri: Uri,
        projectName: String,
        layers: List<Layer>,
        isGreyscale: Boolean,
        onProgress: (Float) -> Unit
    ) {
        var tempFile: File? = null
        try {
            // 1. Create a temporary file in the app's private cache
            val (prefix, ext, mime) = when (exportType) {
                ExportType.SVG -> Triple("export", "svg", "image/svg+xml")
                ExportType.PNG -> Triple("export", "png", "image/png")
            }
            tempFile = ExportOrchestrator.createTempFile(context, prefix, ext)

            // 2. Run the correct exporter, writing to the TEMPORARY file
            when (exportType) {
                ExportType.SVG -> ProjectExporter.exportProjectToSvg(context, layers, Uri.fromFile(tempFile), isGreyscale, onProgress)
                ExportType.PNG -> ProjectExporter.exportProjectToPng(context, layers, Uri.fromFile(tempFile), isGreyscale)
            }

            // 3. If we get here, the job was a success. Finalize the export by moving the temp file.
            val finalFileName = "${projectName}_export.$ext"
            ExportOrchestrator.finalizeExport(context, tempFile, directoryUri, finalFileName, mime)

        } catch (e: Exception) {
            // 4. On ANY failure, re-throw the exception so the UI knows what happened.
            // The finally block will handle cleanup.
            throw e
        } finally {
            // 5. IMPORTANT: Always clean up the temporary file, no matter what.
            tempFile?.delete()
        }
    }
}