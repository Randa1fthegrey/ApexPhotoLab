package com.example.apexphotolab.workspace.toolbars.export.svg._temp_tools

import android.graphics.Bitmap
import com.example.apexphotolab.workspace.toolbars.export.data.TransparencyCrewOrchestrator
import com.example.apexphotolab.workspace.toolbars.export.svg.first_shift.ColorQuantizer
import com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.SecondShiftOrchestrator
import com.example.apexphotolab.workspace.toolbars.export.svg.svg_assembly.AssemblyOrchestrator
import com.example.apexphotolab.workspace.toolbars.export.svg.svg_assembly.final_assembly.SVGAssembler
import com.example.apexphotolab.workspace.toolbars.export.svg.third_shift.ThirdShiftOrchestrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * A temporary diagnostic wrapper around the real SvgGenerator.
 * Its only job is to run the pipeline and log the handoffs between major shifts.
 */
object DiagnosticSvgGenerator {

    suspend fun generate(image: Bitmap, onProgress: (Float) -> Unit): String = withContext(Dispatchers.Default) {
        // SHIFT 1
        onProgress(0.1f)
        val quantizedImage = ColorQuantizer.quantize(image)
        FactoryFloorLogger.logFirstShiftComplete()

        val allSvgElements = coroutineScope {
            val solidColorCrewJob = async {
                // SHIFT 2
                onProgress(0.4f)
                FactoryFloorLogger.logSecondShiftStart()
                // Correctly destructure the Pair returned by the Second Shift.
                val (pathFragments, allEdges) = SecondShiftOrchestrator.run(quantizedImage)
                FactoryFloorLogger.logSecondShiftComplete(pathFragments.size)

                // SHIFT 3
                onProgress(0.7f)
                FactoryFloorLogger.logThirdShiftStart()
                val pathColors = ThirdShiftOrchestrator.run(pathFragments, quantizedImage)
                FactoryFloorLogger.logThirdShiftComplete(pathColors.size)

                // NEW ASSEMBLY SHIFT
                AssemblyOrchestrator.run(pathFragments, pathColors, allEdges)
            }

            val transparencyCrewJob = async {
                TransparencyCrewOrchestrator.run(quantizedImage)
            }

            solidColorCrewJob.await() + transparencyCrewJob.await()
        }

        // FINAL ASSEMBLY
        onProgress(0.9f)
        val finalSvg = SVGAssembler.assemble(allSvgElements, image.width, image.height)

        // DIAGNOSTIC SCAN
        SvgAttributeScanner.scan(finalSvg)

        return@withContext finalSvg
    }
}