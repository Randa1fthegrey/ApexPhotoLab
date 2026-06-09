package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.dispatchers

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.VPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.vps_jobs.VPS_job3
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.VPS_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.census_takers.CensusReport
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.VRAM_Garage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Job: Coloring Dispatcher.
 * Responsibility: Distributing path coloring work across persistent VPS workers in parallel.
 */
object ColoringDispatcher {

    suspend fun resolveColorsInParallel(
        paths: List<List<Point>>,
        quantizedImage: Bitmap
    ): List<CensusReport> = withContext(Dispatchers.Default) {

        val hiredCrew = VPS_HiringDepartment.hireWorkers()

        if (hiredCrew.isEmpty()) return@withContext emptyList()

        VPS_Audit.logShiftHandoff(3)

        val workSlices = ThirdShiftPathSlicer.createSlices(paths)

        val jobs = workSlices.mapIndexed { index, slice ->
            val (worker, workstation) = hiredCrew[index % hiredCrew.size]

            async(workstation) {
                val results = mutableListOf<Pair<Int, CensusReport>>()
                slice.forEach { (originalIndex, pathData) ->
                    VRAM_Garage.wipeSlot(worker.id)
                    VPS_Audit.logCompute(3, worker.id)

                    val taskData = VPS_job3.CensusData(
                        pathData,
                        quantizedImage,
                        VRAM_Garage.getSlotForManager(worker.id)
                    )
                    worker.runTask(3, taskData)
                    taskData.reportResult?.let { results.add(originalIndex to it) }
                }
                results
            }
        }

        val unorderedResults = jobs.awaitAll().flatten()
        return@withContext unorderedResults.sortedBy { it.first }.map { it.second }
    }
}