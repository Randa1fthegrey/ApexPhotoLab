package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.gradient

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.PathDataGenerator
import java.util.UUID

/**
 * Job: Gradient SVG Artist.
 * Responsibility: Assembling linearGradient and path definitions into valid SVG XML snippets.
 */
object GradientSvgArtist {

    fun createSnippet(
        path: List<Point>,
        p1: Point,
        p2: Point,
        startColor: Int,
        endColor: Int
    ): Pair<String, String> {
        val startHex = String.format("#%06X", 0xFFFFFF and startColor)
        val endHex = String.format("#%06X", 0xFFFFFF and endColor)
        val gradId = "grad_${UUID.randomUUID().toString().take(8)}"

        val gradientDef = """
            <linearGradient id="$gradId" x1="${p1.x}" y1="${p1.y}" x2="${p2.x}" y2="${p2.y}" gradientUnits="userSpaceOnUse">
                <stop offset="0%" stop-color="$startHex" />
                <stop offset="100%" stop-color="$endHex" />
            </linearGradient>
        """.trimIndent()

        val d = PathDataGenerator.generate(path)
        val pathElement = "<path d=\"$d\" fill=\"url(#$gradId)\" stroke=\"url(#$gradId)\" stroke-width=\"0.5\" stroke-linejoin=\"round\" />"

        return Pair(gradientDef, pathElement)
    }
}