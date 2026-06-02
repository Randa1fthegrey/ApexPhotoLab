package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram.VRAM_BlobConverter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram.VRAM_EdgeFinder
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.AlphaPathTracer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.VRAM_Garage

/**
 * Job: Alpha Fill Generator.
 * Responsibility: Generating SVG path elements with transparency for all detected alpha regions.
 */
object AlphaFillGenerator {

    fun generate(alphaInfos: List<AlphaGradientDetector.AlphaGradientInfo>): List<String> {
        val svgSnippets = mutableListOf<String>()
        var gradientIdCounter = 0

        alphaInfos.forEach { info ->
            VRAM_Garage.wipeSlot(VRAM_SLOT_ID)
            val vram = VRAM_Garage.getSlotForManager(VRAM_SLOT_ID)

            VRAM_BlobConverter.convertToVRAM(info.blob, vram, info.width)

            val edges = VRAM_EdgeFinder.findEdgesVRAM(vram, info.width, info.height)
            val paths = AlphaPathTracer.trace(edges.toList(), vram, info.width, info.pixels)

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
                append("<path d=\"${openPathData.toString().trim()}\" fill=\"none\" stroke=\"url(#$gradientId)\" stroke-width=\"1.2\" stroke-linecap=\"round\" />")
            }
        }
    }

    private const val VRAM_SLOT_ID = 30
}
