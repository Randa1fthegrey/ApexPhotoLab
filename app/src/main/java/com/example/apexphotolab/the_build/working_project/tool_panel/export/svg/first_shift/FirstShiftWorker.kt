package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift

import java.nio.ByteBuffer

/**
 * Job: Worker Contract Definition.
 * Responsibility: Defining the shared interface for all VRAM-aware parallel first shift workers.
 */
interface FirstShiftWorker {
    val id: Int
    
    /**
     * Processes a slice of the image, returning 10 lists of pixel indices sorted by color group.
     */
    suspend fun processSlice(
        sourcePixels: IntArray,
        targetPixels: IntArray,
        slice: IntRange,
        vramSlot: ByteBuffer 
    ): List<List<Int>>
}
