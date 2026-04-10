package com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift

import com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.census_takers.*
import com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.color_blending.ThirdShiftCensusTaker
import com.example.apexphotolab.workspace.tool_panel.export.svg.utils.CoreChecker
import com.example.apexphotolab.workspace.tool_panel.export.svg.utils.CoreHighwayFactory
import kotlinx.coroutines.CoroutineDispatcher

/**
 * The "Hiring Department" for the Third Shift.
 */
object ThirdShiftHiringDepartment {

    private val allCensusTakers: List<ThirdShiftCensusTaker> = listOf(
        CensusTaker1, CensusTaker2, CensusTaker3, CensusTaker4, CensusTaker5,
        CensusTaker6, CensusTaker7, CensusTaker8, CensusTaker9, CensusTaker10,
        CensusTaker11, CensusTaker12, CensusTaker13, CensusTaker14, CensusTaker15,
        CensusTaker16, CensusTaker17, CensusTaker18, CensusTaker19, CensusTaker20,
        CensusTaker21, CensusTaker22, CensusTaker23, CensusTaker24, CensusTaker25,
        CensusTaker26, CensusTaker27, CensusTaker28, CensusTaker29, CensusTaker30
    )

    fun hireCensusTakers(): List<Pair<ThirdShiftCensusTaker, CoroutineDispatcher>> {
        val numCores = CoreChecker.coreCount
        if (numCores == 0) return emptyList()

        val hiredCensusTakers = allCensusTakers.take(numCores)
        val workstations = CoreHighwayFactory.coreHighways.take(numCores)

        return hiredCensusTakers.zip(workstations)
    }
}
