package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.multicolor_team

import android.graphics.Point

/**
 * Job: Multicolor Blob.
 * Responsibility: Carrying the detected pixel indices, color group membership, and edge points of a spatially unified multi-color shape.
 */
data class MulticolorBlob(
    val pixelIndices: Set<Int>,
    val colorGroupIds: Set<Int>,
    val edgePoints: Set<Point>
)