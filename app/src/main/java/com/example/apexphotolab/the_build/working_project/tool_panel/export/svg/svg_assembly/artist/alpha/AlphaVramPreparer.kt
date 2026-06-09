package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.alpha

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram.VRAM_BlobConverter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.AlphaGradientDetector
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.VRAM_Garage
import java.nio.ByteBuffer

/**
 * Job: Alpha VRAM Preparer.
 * Responsibility: Wiping a VRAM slot and populating it with blob data for alpha analysis.
 */
object AlphaVramPreparer {

    private const val VRAM_SLOT_ID = 30

    fun prepare(info: AlphaGradientDetector.AlphaGradientInfo): ByteBuffer {
        VRAM_Garage.wipeSlot(VRAM_SLOT_ID)
        val vram = VRAM_Garage.getSlotForManager(VRAM_SLOT_ID)
        VRAM_BlobConverter.convertToVRAM(info.blob, vram, info.width)
        return vram
    }
}