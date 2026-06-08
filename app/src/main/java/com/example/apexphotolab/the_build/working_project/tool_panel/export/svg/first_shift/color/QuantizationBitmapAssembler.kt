package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color

import android.graphics.Bitmap

/**
 * Job: Bitmap Assembler.
 * Responsibility: Creating a final ARGB_8888 Bitmap from a target pixel array.
 */
object QuantizationBitmapAssembler {

    fun assemble(pixels: IntArray, width: Int, height: Int): Bitmap {
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }
}
