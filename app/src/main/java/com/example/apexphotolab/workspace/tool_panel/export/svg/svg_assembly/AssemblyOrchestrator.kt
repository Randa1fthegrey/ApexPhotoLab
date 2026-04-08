package com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist.SolidFillGenerator
import com.example.apexphotolab.workspace.tool_panel.export.svg.utils.CoreHighwayFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * The main orchestrator for the Assembly Shift.
 * Standardized Version: Renders all shapes as solid fills to establish
 * structural bedrock before applying visual effects.
 */
object AssemblyOrchestrator {

    suspend fun run(
        pathFragments: List<List<Point>>,
        pathColors: List<Int>,
        sourceImage: Bitmap
    ): List<String> = coroutineScope {

        val highways = CoreHighwayFactory.coreHighways

        // 1. Group fragments by their EXACT color
        val groupsByExactColor = pathFragments.indices.groupBy { pathColors[it] }

        // 2. LAYERED Z-ORDER
        // Priority: 0 (Vibrant Fills) -> 1 (White Fill) -> 2 (Grey Outline) -> 3 (Black Detail)
        val sortedColors = groupsByExactColor.keys.sortedWith(compareBy<Int> { color ->
            val r = android.graphics.Color.red(color); val g = android.graphics.Color.green(color); val b = android.graphics.Color.blue(color)
            val isWhite = r > 240 && g > 240 && b > 240
            val isBlack = r < 20 && g < 20 && b < 20
            val isGrey = !isWhite && !isBlack && r == g && g == b

            when {
                isBlack -> 3
                isGrey -> 2
                isWhite -> 1
                else -> 0
            }
        }.thenByDescending { color ->
            groupsByExactColor[color]?.sumOf { pathFragments[it].size } ?: 0
        })

        // 3. Parallel Painting
        val jobs = sortedColors.mapIndexed { index, color ->
            val fragmentIndices = groupsByExactColor[color] ?: emptyList()
            val highway = if (highways.isNotEmpty()) highways[index % highways.size] else coroutineContext

            async(highway) {
                if (fragmentIndices.isNotEmpty()) {
                    val colorFragments = fragmentIndices.map { pathFragments[it] }
                    // Render everything as solid to ensure border and shape integrity
                    SolidFillGenerator.generate(colorFragments, color)
                } else ""
            }
        }

        return@coroutineScope jobs.awaitAll().filter { it.isNotEmpty() }
    }
}
