package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.blending

/**
 * Job: Color Blending Bucket Labeller.
 * Responsibility: Providing human-readable SVG comment labels for color-family groups.
 */
object ColorBlendingBucketLabeller {

    fun getLabel(index: Int): String {
        return when (index) {
            0 -> "RED BLOBS"
            1 -> "GREEN BLOBS"
            2 -> "BLUE BLOBS"
            3 -> "YELLOW BLOBS"
            4 -> "CYAN BLOBS"
            5 -> "MAGENTA BLOBS"
            6 -> "WHITE BLOBS"
            7 -> "ALPHA BLOBS"
            8 -> "BLACK BLOBS"
            9 -> "GREY BLOBS"
            else -> "UNKNOWN BLOBS"
        }
    }
}