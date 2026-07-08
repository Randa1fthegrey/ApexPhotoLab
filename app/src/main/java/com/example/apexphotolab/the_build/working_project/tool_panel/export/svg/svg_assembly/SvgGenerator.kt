package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly

import android.graphics.Bitmap
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.SVG_Unified_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.ColorQuantizer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.SecondShiftOrchestrator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.final_assembly.SVGAssembler
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.ThirdShiftOrchestrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * Job: SVG Generator.
 * Responsibility: Orchestrating the full SVG generation pipeline across all shifts from image input to final SVG string using the VPS.
 */
object SvgGenerator {

    suspend fun generate(image: Bitmap, onProgress: (Float) -> Unit): String =
        withContext(Dispatchers.Default) {
            SVG_Unified_Audit.start()
            onProgress(val_util.PROGRESS_START)
            
            // 1. FIRST SHIFT: Quantization and Sorting (VPS Job 1)
            SVG_Unified_Audit.logPhase("First Shift", "Quantization")
            val (quantizedImage, colorBuckets) = ColorQuantizer.quantize(image)

            val finalSvgElements = coroutineScope {
                onProgress(val_util.PROGRESS_TRACING)
                
                // 2. SECOND SHIFT: Tracing (VPS Job 2)
                SVG_Unified_Audit.logPhase("Second Shift", "Tracing")
                val (pathFragments, allEdges) = SecondShiftOrchestrator.run(quantizedImage, image, colorBuckets)

                onProgress(val_util.PROGRESS_CONSOLIDATION)
                
                // 3. THIRD SHIFT: Consolidation and Census (VPS Job 3)
                SVG_Unified_Audit.logPhase("Third Shift", "Solidification")
                val (consolidatedPaths, censusReports) = ThirdShiftOrchestrator.run(pathFragments, quantizedImage, image)

                // 4. ASSEMBLY: Final Color Blending (VPS Job 4)
                SVG_Unified_Audit.logPhase("Final Assembly", "Artist Rendering")
                AssemblyOrchestrator.run(consolidatedPaths, censusReports, quantizedImage, image)
            }

            onProgress(val_util.PROGRESS_ASSEMBLY)
            SVG_Unified_Audit.logHandoff("SvgGenerator", "SVGAssembler", "Wrapping XML")
            val finalSvg = SVGAssembler.assemble(finalSvgElements, image.width, image.height)
            
            SVG_Unified_Audit.finalizeAndReport(finalSvg)

            return@withContext finalSvg
        }
}
