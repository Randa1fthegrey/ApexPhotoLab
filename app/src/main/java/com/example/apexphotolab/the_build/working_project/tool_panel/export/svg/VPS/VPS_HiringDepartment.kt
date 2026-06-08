package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.workers.*
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.CoreChecker
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.CoreHighwayFactory
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Job: VPS Hiring Department.
 * Responsibility: Managing the persistent squad of 30 VPS Workers and pairing them with CoreHighways.
 */
object VPS_HiringDepartment {

    private val allWorkers: List<VPS_Interface> = listOf(
        VPS_Worker1, VPS_Worker2, VPS_Worker3, VPS_Worker4, VPS_Worker5,
        VPS_Worker6, VPS_Worker7, VPS_Worker8, VPS_Worker9, VPS_Worker10,
        VPS_Worker11, VPS_Worker12, VPS_Worker13, VPS_Worker14, VPS_Worker15,
        VPS_Worker16, VPS_Worker17, VPS_Worker18, VPS_Worker19, VPS_Worker20,
        VPS_Worker21, VPS_Worker22, VPS_Worker23, VPS_Worker24, VPS_Worker25,
        VPS_Worker26, VPS_Worker27, VPS_Worker28, VPS_Worker29, VPS_Worker30
    )

    fun hireWorkers(): List<Pair<VPS_Interface, CoroutineDispatcher>> {
        val numCores = CoreChecker.coreCount
        if (numCores == 0) return emptyList()

        val hiredWorkers = allWorkers.take(numCores)
        val workstations = CoreHighwayFactory.coreHighways.take(numCores)

        return hiredWorkers.zip(workstations)
    }

    fun getWorkerById(id: Int): VPS_Interface {
        return allWorkers[(id - 1) % allWorkers.size]
    }
}
