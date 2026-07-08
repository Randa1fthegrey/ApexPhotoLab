package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift

import android.graphics.Bitmap
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.Pipeline_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.orchestration.FirstShiftSlicer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.orchestration.WorkDispatcher
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.specialists.QuantizationBitmapAssembler
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.specialists.QuantizationIntensitySorter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.specialists.QuantizationPixelExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Job: Color Quantization Orchestrator.
 * Responsibility: Coordinating specialized workers to perform extraction, quantization, intensity sorting, and assembly.
 */
object ColorQuantizer {

    suspend fun quantize(image: Bitmap): Pair<Bitmap, List<List<Int>>> =
        withContext(Dispatchers.Default) {
            Pipeline_Audit.logHandoff("ColorQuantizer", "Extraction/Dispatch")
            // 1. EXTRACTION
            val sourcePixels = QuantizationPixelExtractor.extract(image)
            val targetPixels = IntArray(sourcePixels.size)

            // 2. ORCHESTRATION (Work Units)
            val workSlices = FirstShiftSlicer.createSlices(sourcePixels.size)

            // 3. DISPATCHING (The Swarm)
            val unsortedBuckets = WorkDispatcher.dispatch(workSlices, sourcePixels, targetPixels)

            // 4. INTENSITY SORTING (Slope Generation)
            Pipeline_Audit.logHandoff("ColorQuantizer", "QuantizationIntensitySorter")
            val sortedBuckets = QuantizationIntensitySorter.sort(unsortedBuckets, sourcePixels)

            // 5. ASSEMBLY
            Pipeline_Audit.logHandoff("ColorQuantizer", "QuantizationBitmapAssembler")
            val bitmap =
                QuantizationBitmapAssembler.assemble(targetPixels, image.width, image.height)

            return@withContext Pair(bitmap, sortedBuckets)
        }
}
