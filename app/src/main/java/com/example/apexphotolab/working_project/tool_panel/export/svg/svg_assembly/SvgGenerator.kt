package com.example.apexphotolab.working_project.tool_panel.export.svg.svg_assembly

import android.graphics.Bitmap
import com.example.apexphotolab.working_project.tool_panel.export.svg._temp_tools.HandoffLogger
import com.example.apexphotolab.working_project.tool_panel.export.svg._temp_tools.ShapeCounter
import com.example.apexphotolab.working_project.tool_panel.export.data.TransparencyCrewOrchestrator
import com.example.apexphotolab.working_project.tool_panel.export.svg.first_shift.ColorQuantizer
import com.example.apexphotolab.working_project.tool_panel.export.svg.second_shift.SecondShiftOrchestrator
import com.example.apexphotolab.working_project.tool_panel.export.svg.svg_assembly.final_assembly.SVGAssembler
import com.example.apexphotolab.working_project.tool_panel.export.svg.third_shift.ThirdShiftOrchestrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * The Master Orchestrator (CEO) of the SVG Generation process.
 */
object SvgGenerator {

    suspend fun generate(image: Bitmap, onProgress: (Float) -> Unit): String =
        withContext(Dispatchers.Default) {
            onProgress(0.1f)
            val quantizedImage = ColorQuantizer.quantize(image)
            HandoffLogger.logShift1to2(quantizedImage.width, quantizedImage.height)

            val allSvgElements = coroutineScope {
                val solidColorCrewJob = async {
                    onProgress(0.4f)
                    val (pathFragments, allEdges) = SecondShiftOrchestrator.run(quantizedImage, image)
                    HandoffLogger.logShift2to3(pathFragments.size, allEdges.size)

                    onProgress(0.7f)
                    val (consolidatedPaths, censusReports) = ThirdShiftOrchestrator.run(pathFragments, quantizedImage, image)
                    HandoffLogger.logShift3toAssembly(censusReports.size)

                    AssemblyOrchestrator.run(consolidatedPaths, censusReports, image)
                }

                val transparencyCrewJob = async {
                    TransparencyCrewOrchestrator.run(quantizedImage)
                }

                transparencyCrewJob.await() + solidColorCrewJob.await()
            }

            HandoffLogger.logAssemblyToFinish(allSvgElements.size)

            onProgress(0.9f)
            val finalSvg = SVGAssembler.assemble(allSvgElements, image.width, image.height)
            
            // SHUT OFF OLD DIAGNOSTICS - ACTIVATE SHAPE COUNTER
            ShapeCounter.log(finalSvg)

            return@withContext finalSvg
        }
}
