package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.color_resolution

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.CVPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs.CVPS_job1
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.CVPS_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.palette.ValueClamper
import kotlinx.coroutines.runBlocking

/**
 * Job: Third Shift Color Palette.
 * Responsibility: Providing the color palette and range lookups for the Third Shift using CVPS ramps.
 */
object ThirdShiftColorPalette {

    val PALETTE: IntArray = runBlocking { createColorPalette() }

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

    private suspend fun createColorPalette(): IntArray {
        val rawPalette = mutableListOf<Int>()

        for (i in 0 until 10) {
            val worker = CVPS_HiringDepartment.getWorkerByColorId(i)
            CVPS_Audit.logCompute(1, i)
            val data = CVPS_job1.RampData()
            worker.runColorTask(1, data)
            rawPalette.addAll(data.result)
        }

        val finalPalette = ValueClamper.apply(rawPalette)
        return finalPalette.toIntArray()
    }

    private fun getStartIndex(rampIndex: Int) = rampIndex * RAMP_SIZE

    private const val RAMP_SIZE = 128
}