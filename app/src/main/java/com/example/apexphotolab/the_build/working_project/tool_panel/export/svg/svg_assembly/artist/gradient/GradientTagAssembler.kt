package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.gradient

/**
 * Job: Gradient Tag Assembler.
 * Responsibility: Building the final linearGradient and path XML elements for a gradient shape.
 */
object GradientTagAssembler {

    fun assemble(
        info: GradientFillGenerator.GradientInfo,
        coords: GradientVectorMath.GradientCoords,
        partition: GradientPathPartitioner.PartitionedPaths,
        startHex: String,
        endHex: String
    ): String {
        val gradientDef = """
    <linearGradient id="${info.id}" x1="${coords.x1}" y1="${coords.y1}" x2="${coords.x2}" y2="${coords.y2}">
      <stop offset="0%" style="stop-color:$startHex;stop-opacity:1" />
      <stop offset="100%" style="stop-color:$endHex;stop-opacity:1" />
    </linearGradient>"""

        return buildString {
            append(gradientDef)
            if (partition.closedData.isNotEmpty()) {
                append("\n<path d=\"${partition.closedData}\" fill=\"url(#${info.id})\" fill-rule=\"evenodd\" />")
            }
            if (partition.openData.isNotEmpty()) {
                append("\n<path d=\"${partition.openData}\" fill=\"none\" stroke=\"url(#${info.id})\" stroke-width=\"1.2\" stroke-linecap=\"round\" />")
            }
        }
    }
}