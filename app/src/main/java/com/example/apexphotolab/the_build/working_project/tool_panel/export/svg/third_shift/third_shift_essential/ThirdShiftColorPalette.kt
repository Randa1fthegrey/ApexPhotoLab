package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.third_shift_essential

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ValueClamper
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ramps.AlphaRamp
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ramps.BlackRamp
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ramps.BlueRamp
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ramps.CyanRamp
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ramps.GreenRamp
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ramps.GreyRamp
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ramps.MagentaRamp
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ramps.RedRamp
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ramps.WhiteRamp
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color.ramps.YellowRamp

/**
 * Job: Third Shift Color Palette.
 * Responsibility: Providing the color palette and range lookups for the Third Shift,
 * mirroring the main ColorPalette to ensure indices match across shifts.
 */
object ThirdShiftColorPalette {

    val PALETTE: IntArray = createColorPalette()

    fun getRedRange(): IntRange = getStartIndex(0) until getStartIndex(1)
    fun getGreenRange(): IntRange = getStartIndex(1) until getStartIndex(2)
    fun getBlueRange(): IntRange = getStartIndex(2) until getStartIndex(3)
    fun getYellowRange(): IntRange = getStartIndex(3) until getStartIndex(4)
    fun getCyanRange(): IntRange = getStartIndex(4) until getStartIndex(5)
    fun getMagentaRange(): IntRange = getStartIndex(5) until getStartIndex(6)
    fun getWhiteRange(): IntRange = getStartIndex(6) until getStartIndex(7)
    fun getAlphaRange(): IntRange = getStartIndex(7) until getStartIndex(8)
    fun getBlackRange(): IntRange = getStartIndex(8) until getStartIndex(9)
    fun getGreyRange(): IntRange = getStartIndex(9) until getStartIndex(10)

    private fun createColorPalette(): IntArray {
        val rawPalette = mutableListOf<Int>()
        rawPalette.addAll(RedRamp.generate())     // 0
        rawPalette.addAll(GreenRamp.generate())   // 1
        rawPalette.addAll(BlueRamp.generate())    // 2
        rawPalette.addAll(YellowRamp.generate())  // 3
        rawPalette.addAll(CyanRamp.generate())    // 4
        rawPalette.addAll(MagentaRamp.generate()) // 5
        rawPalette.addAll(WhiteRamp.generate())   // 6
        rawPalette.addAll(AlphaRamp.generate())   // 7
        rawPalette.addAll(BlackRamp.generate())   // 8
        rawPalette.addAll(GreyRamp.generate())    // 9

        val finalPalette = ValueClamper.apply(rawPalette)
        return finalPalette.toIntArray()
    }

    private fun getStartIndex(rampIndex: Int) = rampIndex * RAMP_SIZE

    private const val RAMP_SIZE = 128
}