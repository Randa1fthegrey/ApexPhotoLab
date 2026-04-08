package com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist

import android.graphics.Color
import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg._temp_tools.PathLabeller
import com.example.apexphotolab.workspace.tool_panel.export.svg._temp_tools.XRayControl

/**
 * File 6 (Artist Phase): The Solid Shape Fill Generator.
 * Updated: Precision Choking Protocol. Ensures fills stay inside outlines by using
 * a wider overhang for Grey/Black structural frames.
 * Updated with X-Ray Diagnostic Support.
 */
object SolidFillGenerator {

    fun generate(paths: List<List<Point>>, color: Int): String {
        if (paths.isEmpty() || paths.first().isEmpty()) return ""

        val alpha = Color.alpha(color)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val colorHex = String.format("#%06X", 0xFFFFFF and color)
        
        // X-RAY TOGGLE: Force transparency if diagnostics are on
        val opacity = if (XRayControl.IS_XRAY_ENABLED) 1.0 else alpha / 255.0
        val fillAttr = if (XRayControl.IS_XRAY_ENABLED) "none" else colorHex

        // Identification: Is this a Fill (White) or a Structural Outline (Grey/Black)?
        val isWhiteFill = r > 240 && g > 240 && b > 240
        val isStructuralOutline = (r == g && g == b && r < 200) // Deep Greys and Blacks

        // WIDER OVERHANG: Use a 2.0px stroke for outlines to "trap" the 0-stroke fills.
        val strokeWidth = when {
            isWhiteFill -> "0.0"
            isStructuralOutline -> "2.0"
            else -> "1.0"
        }

        val closedPathData = StringBuilder()
        val openPathData = StringBuilder()
        val diagnosticLabels = StringBuilder()

        paths.forEach { path ->
            val (data, isClosed) = PathDataGenerator.generateWithStatus(path)
            if (isClosed) {
                closedPathData.append(data).append(" ")
            } else {
                openPathData.append(data).append(" ")
            }
            
            // Generate corner labels if X-Ray is on
            if (XRayControl.IS_XRAY_ENABLED) {
                diagnosticLabels.append(PathLabeller.label(path, color))
            }
        }

        return buildString {
            if (closedPathData.isNotEmpty()) {
                append("<path d=\"${closedPathData.toString().trim()}\" fill=\"$fillAttr\" fill-opacity=\"$opacity\" stroke=\"$colorHex\" stroke-opacity=\"$opacity\" stroke-width=\"$strokeWidth\" stroke-linejoin=\"round\" stroke-linecap=\"round\" fill-rule=\"evenodd\" />")
            }
            if (openPathData.isNotEmpty()) {
                if (isNotEmpty()) append("\n")
                append("<path d=\"${openPathData.toString().trim()}\" fill=\"none\" stroke=\"$colorHex\" stroke-opacity=\"$opacity\" stroke-width=\"1.2\" stroke-linejoin=\"round\" stroke-linecap=\"round\" />")
            }
            
            // Add the numbered labels on top of the paths
            if (diagnosticLabels.isNotEmpty()) {
                append("\n").append(diagnosticLabels.toString())
            }
        }
    }
}
