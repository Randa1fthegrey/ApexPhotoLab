package com.example.apexphotolab.workspace.toolbars.export.svg.svg_assembly.final_assembly

/**
 * File 10 (Final Assembly): The SVG Assembler.
 * Combines all generated SVG elements into a single, valid SVG document.
 */
object SVGAssembler {

    /**
     * Assembles the final SVG document from all generated parts.
     * @param svgElements A list of all SVG element strings (paths, defs, etc.).
     * @param width The width of the SVG canvas.
     * @param height The height of the SVG canvas.
     * @return A single string containing the complete SVG document.
     */
    fun assemble(
        svgElements: List<String>,
        width: Int,
        height: Int
    ): String {
        val defs = mutableListOf<String>()
        val paths = mutableListOf<String>()

        // Separate definitions from drawable paths
        svgElements.forEach {
            val trimmed = it.trim()
            if (trimmed.startsWith("<linearGradient")) {
                defs.add(trimmed)
            } else if (trimmed.startsWith("<path")) {
                paths.add(trimmed)
            }
        }

        return buildString {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n")
            append("<svg width=\"$width\" height=\"$height\" xmlns=\"http://www.w3.org/2000/svg\">\n")

            // Write all definitions if there are any
            if (defs.isNotEmpty()) {
                append("  <defs>\n")
                defs.forEach { def ->
                    // Indent for readability
                    def.lines().forEach { line -> append("    $line\n") }
                }
                append("  </defs>\n")
            }

            // Write all path elements
            paths.forEach { path ->
                append("  $path\n")
            }

            append("</svg>")
        }
    }
}