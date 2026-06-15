package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.final_assembly

/**
 * Final Assembly Value Utility.
 * Responsibility: Centralizing XML tags and document structure strings for the final SVG output.
 */
object val_util {

    // ==========================================
    // XML DOCUMENT STRUCTURE
    // ==========================================

    val HEADER_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
    val TAG_SVG_START_PREFIX = "<svg width=\""
    val TAG_SVG_START_MIDDLE = "\" height=\""
    val TAG_SVG_START_SUFFIX = "\" xmlns=\"http://www.w3.org/2000/svg\">\n"
    val TAG_SVG_END = "</svg>"

    val TAG_DEFS_START = "  <defs>\n"
    val TAG_DEFS_END = "  </defs>\n"

    // ==========================================
    // ELEMENT IDENTIFIERS
    // ==========================================

    val TAG_GRADIENT_CLOSE = "</linearGradient>"
    val TAG_GRADIENT_START = "<linearGradient"
    val TAG_PATH_START = "<path"
    val TAG_COMMENT_START = "<!--"
}
