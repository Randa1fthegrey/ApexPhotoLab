package com.example.apexphotolab.workspace.toolbars.export.svg.svg_assembly.artist

import android.graphics.Point
import com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.EdgeFindingCrew
import com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.tracers.AlphaPathTracer
import com.example.apexphotolab.workspace.toolbars.export.svg.utils.VRAM_Garage

/**
 * File 9 (Artist Phase): The Alpha Fill Generator.
 * Takes alpha gradient reports and generates the final drawable SVG paths with transparency.
 * VRAM-Optimized: Uses a dedicated VRAM slot for edge detection to prevent OOM.
 */
object AlphaFillGenerator {

    private const val VRAM_SLOT_ID = 30 // Dedicated slot for the Alpha Generator

    /**
     * Generates SVG elements for all detected transparency regions.
     * @param alphaInfos The list of reports from the AlphaGradientDetector.
     * @return A list of strings, each containing a complete SVG snippet (<defs> and <path>).
     */
    fun generate(alphaInfos: List<AlphaGradientDetector.AlphaGradientInfo>): List<String> {
        val svgSnippets = mutableListOf<String>()
        var gradientIdCounter = 0
        
        // Grab a dedicated VRAM slot for safe edge finding
        val vram = VRAM_Garage.getSlotForManager(VRAM_SLOT_ID)

        alphaInfos.forEach { info ->
            // Use the VRAM-powered EdgeFindingCrew to keep heap usage near zero
            val edges = EdgeFindingCrew.findEdges(info.blob, info.width, info.height, vram)
            val paths = AlphaPathTracer.trace(edges, info.blob)

            if (paths.isNotEmpty()) {
                val gradientId = "alphaGrad${gradientIdCounter++}"
                val snippet = generateSvgSnippet(paths, info, gradientId)
                svgSnippets.add(snippet)
            }
        }

        return svgSnippets
    }

    private fun generateSvgSnippet(
        paths: List<List<Point>>,
        info: AlphaGradientDetector.AlphaGradientInfo,
        gradientId: String
    ): String {
        val gradientDef = buildGradientDefinition(info, gradientId)
        val pathElements = buildPathElements(paths, gradientId)
        return "$gradientDef\n$pathElements"
    }

    private fun buildGradientDefinition(info: AlphaGradientDetector.AlphaGradientInfo, gradientId: String): String {
        val startOpacity = info.startAlpha / 255.0
        val endOpacity = info.endAlpha / 255.0

        val (x1, y1, x2, y2) = when (info.direction) {
            GradientFillGenerator.GradientDirection.HORIZONTAL -> Triple("0%", "0%", "100%").let { arrayOf(it.first, it.second, it.third, it.second) }
            GradientFillGenerator.GradientDirection.VERTICAL -> Triple("0%", "0%", "0%").let { arrayOf(it.first, it.second, it.third, "100%") }
            else -> arrayOf("0%", "0%", "100%", "100%")
        }

        return """
    <linearGradient id="$gradientId" x1="$x1" y1="$y1" x2="$x2" y2="$y2">
      <stop offset="0%" style="stop-color:black;stop-opacity:$startOpacity" />
      <stop offset="100%" style="stop-color:black;stop-opacity:$endOpacity" />
    </linearGradient>"""
    }

    private fun buildPathElements(paths: List<List<Point>>, gradientId: String): String {
        val closedPathData = StringBuilder()
        val openPathData = StringBuilder()

        paths.forEach { path ->
            val (data, isClosed) = PathDataGenerator.generateWithStatus(path)
            if (isClosed) {
                closedPathData.append(data).append(" ")
            } else {
                openPathData.append(data).append(" ")
            }
        }

        return buildString {
            if (closedPathData.isNotEmpty()) {
                append("<path d=\"${closedPathData.toString().trim()}\" fill=\"url(#$gradientId)\" fill-rule=\"evenodd\" />")
            }
            if (openPathData.isNotEmpty()) {
                if (isNotEmpty()) append("\n")
                // Open paths get stroke with the gradient and NO fill to prevent chords.
                append("<path d=\"${openPathData.toString().trim()}\" fill=\"none\" stroke=\"url(#$gradientId)\" stroke-width=\"1.2\" stroke-linecap=\"round\" />")
            }
        }
    }
}
