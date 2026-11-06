package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.Vector_Sanitizer

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.orchestration.ColorQuantizer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.dispatchers.SecondShiftDispatcher
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census.CensusReport
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.orchestration.ThirdShiftOrchestrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Job: Vector Trace Orchestrator.
 * Responsibility: Wires the NES 256x240 image into the SVG Tracing Pipeline (Shifts 1, 2, 3),
 * extracts the solidified vector paths, and delegates to the NesVectorPainter to create a
 * perfect, razor-sharp 256x240 bitmap ready for tile slicing.
 */
object NesVectorTraceManager {

    suspend fun executeTraceAndPaint(
        nesCanvas: Bitmap,
        universalBgColor: Int,
        active13Colors: IntArray
    ): Bitmap = withContext(Dispatchers.Default) {

        // --- 1. SVG FIRST SHIFT: Color Quantization (using our custom NES 13 colors) ---
        // Instead of the standard SVG palette, we use the active13Colors extracted earlier!
        val width = nesCanvas.width
        val height = nesCanvas.height
        val rawPixels = IntArray(width * height)
        nesCanvas.getPixels(rawPixels, 0, width, 0, 0, width, height)

        val firstShiftPixels = mutableListOf<List<Int>>()
        for (i in 0 until 10) { // Limit to standard 10 buckets for pipeline compatibility
            val bucketList = mutableListOf<Int>()
            val targetColor = active13Colors.getOrElse(i) { active13Colors[0] }

            for (pxIdx in rawPixels.indices) {
                if (rawPixels[pxIdx] == targetColor && targetColor != universalBgColor) {
                    bucketList.add(pxIdx)
                }
            }
            firstShiftPixels.add(bucketList)
        }

        // --- 2. SVG SECOND SHIFT: Edge Scanning & Tracing ---
        // This generates the raw structural wireframe loops for the circuits and letters
        val (secondShiftPaths, originColors, allEdges) = SecondShiftDispatcher.traceInParallel(
            firstShiftPixels, width, height, rawPixels, emptySet()
        )

        // --- 3. SVG THIRD SHIFT: Solidification & Color Grouping ---
        // This closes gaps, solidifies the interiors, and groups shapes by color family
        val (finalPaths, finalReports) = ThirdShiftOrchestrator.run(
            secondShiftPaths, originColors, nesCanvas, nesCanvas
        )

        // Organize into Map for the Painter
        val groupedForPainter = mutableMapOf<CensusReport, List<List<android.graphics.Point>>>()
        for (i in finalPaths.indices) {
            val report = finalReports[i]
            val path = finalPaths[i]
            val existing = groupedForPainter[report]?.toMutableList() ?: mutableListOf()
            existing.add(path)
            groupedForPainter[report] = existing
        }

        // --- 4. NES VECTOR PAINTER ---
        // Paint the pristine, 100% solid vector shapes onto a fresh 256x240 bitmap!
        return@withContext NesVectorPainter.paintPathsToBitmap(
            width, height, universalBgColor, groupedForPainter
        )
    }
}
