package com.example.apexphotolab.working_project.tool_panel.export.svg.first_shift.color

import com.example.apexphotolab.working_project.tool_panel.export.svg.first_shift.color.ramps.*

/**
 * The "Conductor"
 * Assembles the final palette from the 10 specialized color ramp files.
 * Synchronized Version: Ensures indices match all teams and shifts.
 */
object ColorPalette {

    val PALETTE: IntArray = createColorPalette()

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

    private const val RAMP_SIZE = 32
    private fun getStartIndex(rampIndex: Int) = rampIndex * RAMP_SIZE

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
}
