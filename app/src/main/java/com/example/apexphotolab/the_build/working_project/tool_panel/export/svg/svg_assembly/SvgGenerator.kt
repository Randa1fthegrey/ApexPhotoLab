package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly

import android.graphics.Bitmap
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.HandoffLogger
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.ShapeCounter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.TransparencyCrewOrchestrator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ColorQuantizer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.SecondShiftOrchestrator
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.final_assembly.SVGAssembler
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.third_shift_essential.ThirdShiftOrchestrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * Job: SVG Generator.
 * Responsibility: Orchestrating the full SVG generation pipeline across all shifts from image input to final SVG string.
 */
object SvgGenerator {

    suspend fun generate(image: Bitmap, onProgress: (Float) -> Unit): String =
        withContext(Dispatchers.Default) {
            onProgress(0.1f)
            
            // 1. FIRST SHIFT: Quantization and Sorting (Swarm-Powered)
            val (quantizedImage, colorBuckets) = ColorQuantizer.quantize(image)
            HandoffLogger.logShift1to2(quantizedImage.width, quantizedImage.height)

            val finalSvgElements = coroutineScope {
                onProgress(0.4f)
                
                // 2. SECOND SHIFT: Tracing (Using pre-sorted intensity-descending buckets)
                val (pathFragments, allEdges) = SecondShiftOrchestrator.run(quantizedImage, image, colorBuckets)
                HandoffLogger.logShift2to3(pathFragments.size, allEdges.size)

                onProgress(0.7f)
                
                // 3. THIRD SHIFT: Consolidation and Census
                val (consolidatedPaths, censusReports) = ThirdShiftOrchestrator.run(pathFragments, quantizedImage, image)
                HandoffLogger.logShift3toAssembly(censusReports.size)

                // 4. ASSEMBLY: Final Color Blending and SVG string generation
                AssemblyOrchestrator.run(consolidatedPaths, censusReports, quantizedImage, image)
            }

            HandoffLogger.logAssemblyToFinish(finalSvgElements.size)

            onProgress(0.9f)
            val finalSvg = SVGAssembler.assemble(finalSvgElements, image.width, image.height)
            ShapeCounter.log(finalSvg)

            return@withContext finalSvg
        }
}
