package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.final_assembly

/**
 * Job: SVG Assembler.
 * Responsibility: Combining all generated SVG elements into a single valid SVG document.
 */
object SVGAssembler {

    fun assemble(
        svgElements: List<String>,
        width: Int,
        height: Int
    ): String {
        val defs = mutableListOf<String>()
        val paths = mutableListOf<String>()

        // Separate definitions from drawable paths
        svgElements.forEach { element ->
            val trimmed = element.trim()
            if (trimmed.contains("</linearGradient>")) {
                // Split it into the definition and the path parts
                val defPart = trimmed.substringBeforeLast("</linearGradient>") + "</linearGradient>"
                val pathPart = trimmed.substringAfterLast("</linearGradient>").trim()

                if (defPart.isNotEmpty()) defs.add(defPart)
                if (pathPart.isNotEmpty()) paths.add(pathPart)
            } else if (trimmed.startsWith("<linearGradient")) {
                defs.add(trimmed)
            } else if (trimmed.startsWith("<path")) {
                paths.add(trimmed)
            } else if (trimmed.startsWith("<!--")) {
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
