package com.example.apexphotolab.workspace.toolbars.export.svg.first_shift

import com.example.apexphotolab.workspace.toolbars.export.svg.first_shift.managers.*
import com.example.apexphotolab.workspace.toolbars.export.svg.utils.CoreChecker
import com.example.apexphotolab.workspace.toolbars.export.svg.utils.CoreHighwayFactory
import kotlinx.coroutines.CoroutineDispatcher

/**
 * The "Hiring Department" for the First Shift.
 */
object HiringDepartment {

    private val allManagers: List<FirstShiftWorker> = listOf(
        Manager1, Manager2, Manager3, Manager4, Manager5, Manager6, Manager7, Manager8, Manager9, Manager10,
        Manager11, Manager12, Manager13, Manager14, Manager15, Manager16, Manager17, Manager18, Manager19, Manager20,
        Manager21, Manager22, Manager23, Manager24, Manager25, Manager26, Manager27, Manager28, Manager29, Manager30
    )

    fun hireManagers(): List<Pair<FirstShiftWorker, CoroutineDispatcher>> {
        val numCores = CoreChecker.coreCount
        if (numCores == 0) return emptyList()

        val hiredManagers = allManagers.take(numCores)
        val workstations = CoreHighwayFactory.coreHighways.take(numCores)

        return hiredManagers.zip(workstations)
    }
}