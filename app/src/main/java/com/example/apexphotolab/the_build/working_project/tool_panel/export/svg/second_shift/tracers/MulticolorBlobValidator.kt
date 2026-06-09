package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers

/**
 * Job: Multicolor Blob Validator.
 * Responsibility: Enforcing constraints on connected regions to determine if they qualify as "Multicolor" shapes.
 */
object MulticolorBlobValidator {

    private const val MIN_COLOR_GROUPS = 2
    private const val MIN_BLOB_SIZE = 50
    private const val MAX_BLOB_SIZE = 100000 // Background protection

    fun isValid(colorGroupIds: Set<Int>, blobSize: Int): Boolean {
        return colorGroupIds.size >= MIN_COLOR_GROUPS &&
               blobSize >= MIN_BLOB_SIZE &&
               blobSize <= MAX_BLOB_SIZE
    }
}