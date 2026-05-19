package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.color_workers.*
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.CoreChecker
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.CoreHighwayFactory
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Job: Worker Hiring Department.
 * Responsibility: Pairing the correct number of first shift workers with their dedicated coroutine dispatchers.
 */
object HiringDepartment {

    private val allWorkers: List<FirstShiftWorker> = listOf(
        ColorWorker1, ColorWorker2, ColorWorker3, ColorWorker4, ColorWorker5, ColorWorker6, ColorWorker7, ColorWorker8, ColorWorker9, ColorWorker10,
        ColorWorker11, ColorWorker12, ColorWorker13, ColorWorker14, ColorWorker15, ColorWorker16, ColorWorker17, ColorWorker18, ColorWorker19, ColorWorker20,
        ColorWorker21, ColorWorker22, ColorWorker23, ColorWorker24, ColorWorker25, ColorWorker26, ColorWorker27, ColorWorker28, ColorWorker29, ColorWorker30
    )

    fun hireWorkers(): List<Pair<FirstShiftWorker, CoroutineDispatcher>> {
        val numCores = CoreChecker.coreCount
        if (numCores == 0) return emptyList()

        val hiredWorkers = allWorkers.take(numCores)
        val workstations = CoreHighwayFactory.coreHighways.take(numCores)

        return hiredWorkers.zip(workstations)
    }
}
