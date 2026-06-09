package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.gradient

import android.graphics.Point

/**
 * Job: Gradient Fill Generator (Orchestrator).
 * Responsibility: Coordinating formatting, math, partitioning, and assembly to produce final SVG gradient snippets.
 */
object GradientFillGenerator {

    data class GradientInfo(
        val id: String,
        val startColor: Int,
        val endColor: Int,
        val direction: GradientDirection
    )

    enum class GradientDirection {
        HORIZONTAL, VERTICAL, DIAGONAL
    }

    fun generate(paths: List<List<Point>>, info: GradientInfo): String {
        // 1. FORMATTING
        val startHex = GradientColorFormatter.toHex(info.startColor)
        val endHex = GradientColorFormatter.toHex(info.endColor)

        // 2. MATH
        val coords = GradientVectorMath.calculate(info.direction)

        // 3. PARTITIONING
        val partition = GradientPathPartitioner.partition(paths)

        // 4. ASSEMBLY
        return GradientTagAssembler.assemble(info, coords, partition, startHex, endHex)
    }
}