package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.gradient

import android.graphics.Bitmap
import android.graphics.Point

/**
 * Job: Gradient Painter (Orchestrator).
 * Responsibility: Coordinating surveying, sampling, and snippet creation for gradient shapes.
 */
object GradientPainter {

    fun paint(path: List<Point>, source: Bitmap): Pair<String, String> {
        val points = path.filter { it.x != -1 }
        if (points.size < 4) return Pair("", "")

        // 1. SURVEYING
        val survey = GradientSurveyor.survey(points) ?: return Pair("", "")

        // 2. SAMPLING
        val startColor = GradientDeepSampler.sample(survey.p1, source, 5)
        val endColor = GradientDeepSampler.sample(survey.p2, source, 5)

        // 3. ARTISTRY
        return GradientSvgArtist.createSnippet(path, survey.p1, survey.p2, startColor, endColor)
    }
}