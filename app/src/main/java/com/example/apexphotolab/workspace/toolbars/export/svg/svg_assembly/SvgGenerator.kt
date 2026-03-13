package com.example.apexphotolab.workspace.toolbars.export.svg.svg_assembly

import android.graphics.Bitmap
import com.example.apexphotolab.workspace.toolbars.export.data.TransparencyCrewOrchestrator
import com.example.apexphotolab.workspace.toolbars.export.svg.first_shift.ColorQuantizer
import com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.SecondShiftOrchestrator
import com.example.apexphotolab.workspace.toolbars.export.svg.svg_assembly.final_assembly.SVGAssembler
import com.example.apexphotolab.workspace.toolbars.export.svg.third_shift.ThirdShiftOrchestrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * The Master Orchestrator (CEO) of the SVG Generation process.
 * Corrected: Ensures transparency/backgrounds are at the bottom of the Z-order.
 */
object SvgGenerator {

    suspend fun generate(image: Bitmap, onProgress: (Float) -> Unit): String =
        withContext(Dispatchers.Default) {
            onProgress(0.1f)
            val quantizedImage = ColorQuantizer.quantize(image)

            val allSvgElements = coroutineScope {
                // Run crews in parallel
                val solidColorCrewJob = async {
                    onProgress(0.4f)
                    val (pathFragments, allEdges) = SecondShiftOrchestrator.run(quantizedImage)
                    onProgress(0.7f)
                    val pathColors = ThirdShiftOrchestrator.run(pathFragments, quantizedImage)
                    AssemblyOrchestrator.run(pathFragments, pathColors, allEdges)
                }

                val transparencyCrewJob = async {
                    TransparencyCrewOrchestrator.run(quantizedImage)
                }

                // IMPORTANT: Transparency/Background results MUST come first in the list
                // so they are written to the SVG first (at the bottom).
                transparencyCrewJob.await() + solidColorCrewJob.await()
            }

            onProgress(0.9f)
            val finalSvg = SVGAssembler.assemble(allSvgElements, image.width, image.height)
            return@withContext finalSvg
        }
}
