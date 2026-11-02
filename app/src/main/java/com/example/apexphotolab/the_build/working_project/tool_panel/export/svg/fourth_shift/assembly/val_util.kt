package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.fourth_shift.assembly

/**
 * Fourth Shift Utility Constants.
 * Responsibility: Centralizing XML tags, document structure strings, stroke weights, and progress markers for Fourth Shift.
 */
object val_util {

    // ==========================================
    // PIPELINE PROGRESS MARKERS
    // ==========================================

    val PROGRESS_START = 0.1f
    val PROGRESS_TRACING = 0.4f
    val PROGRESS_CONSOLIDATION = 0.7f
    val PROGRESS_ASSEMBLY = 0.9f

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

    // ==========================================
    // STROKE WEIGHTS
    // ==========================================

    val STROKE_WHITE = "0.5"
    val STROKE_STRUCTURAL = "0.8"
    val STROKE_DEFAULT = "0.8"

    // ==========================================
    // COLOR THRESHOLDS
    // ==========================================

    val COLOR_WHITE_THRESHOLD = 240
    val COLOR_STRUCTURAL_THRESHOLD = 200

    // ==========================================
    // ALPHA DETECTION
    // ==========================================

    val ALPHA_THRESHOLD = 1
}
