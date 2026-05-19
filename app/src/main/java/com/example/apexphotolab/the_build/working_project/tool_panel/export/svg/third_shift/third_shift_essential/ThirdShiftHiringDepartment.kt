package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.third_shift_essential

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker1
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker10
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker11
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker12
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker13
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker14
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker15
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker16
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker17
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker18
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker19
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker2
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker20
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker21
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker22
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker23
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker24
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker25
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker26
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker27
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker28
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker29
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker3
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker30
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker4
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker5
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker6
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker7
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker8
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusTaker9
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.CoreChecker
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.CoreHighwayFactory
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Job: Third Shift Hiring Department.
 * Responsibility: Pairing Census Takers with CPU core dispatchers based on available hardware.
 */
object ThirdShiftHiringDepartment {

    fun hireCensusTakers(): List<Pair<ThirdShiftCensusTaker, CoroutineDispatcher>> {
        val numCores = CoreChecker.coreCount
        if (numCores <= 0) return emptyList()

        val hiredCensusTakers = allCensusTakers.take(numCores)
        val workstations = CoreHighwayFactory.coreHighways.take(numCores)

        return hiredCensusTakers.zip(workstations)
    }

    private val allCensusTakers: List<ThirdShiftCensusTaker> = listOf(
        CensusTaker1, CensusTaker2, CensusTaker3, CensusTaker4, CensusTaker5,
        CensusTaker6, CensusTaker7, CensusTaker8, CensusTaker9, CensusTaker10,
        CensusTaker11, CensusTaker12, CensusTaker13, CensusTaker14, CensusTaker15,
        CensusTaker16, CensusTaker17, CensusTaker18, CensusTaker19, CensusTaker20,
        CensusTaker21, CensusTaker22, CensusTaker23, CensusTaker24, CensusTaker25,
        CensusTaker26, CensusTaker27, CensusTaker28, CensusTaker29, CensusTaker30
    )
}