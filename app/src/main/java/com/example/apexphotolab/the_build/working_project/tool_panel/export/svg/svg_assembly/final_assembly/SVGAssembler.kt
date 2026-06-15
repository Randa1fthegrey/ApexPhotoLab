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
            if (trimmed.contains(val_util.TAG_GRADIENT_CLOSE)) {
                // Split it into the definition and the path parts
                val defPart = trimmed.substringBeforeLast(val_util.TAG_GRADIENT_CLOSE) + val_util.TAG_GRADIENT_CLOSE
                val pathPart = trimmed.substringAfterLast(val_util.TAG_GRADIENT_CLOSE).trim()

                if (defPart.isNotEmpty()) defs.add(defPart)
                if (pathPart.isNotEmpty()) paths.add(pathPart)
            } else if (trimmed.startsWith(val_util.TAG_GRADIENT_START)) {
                defs.add(trimmed)
            } else if (trimmed.startsWith(val_util.TAG_PATH_START)) {
                paths.add(trimmed)
            } else if (trimmed.startsWith(val_util.TAG_COMMENT_START)) {
                paths.add(trimmed)
            }
        }

        return buildString {
            append(val_util.HEADER_XML)
            append("${val_util.TAG_SVG_START_PREFIX}$width${val_util.TAG_SVG_START_MIDDLE}$height${val_util.TAG_SVG_START_SUFFIX}")

            // Write all definitions if there are any
            if (defs.isNotEmpty()) {
                append(val_util.TAG_DEFS_START)
                defs.forEach { def ->
                    // Indent for readability
                    def.lines().forEach { line -> append("    $line\n") }
                }
                append(val_util.TAG_DEFS_END)
            }

            // Write all path elements
            paths.forEach { path ->
                append("  $path\n")
            }

            append(val_util.TAG_SVG_END)
        }
    }
}
