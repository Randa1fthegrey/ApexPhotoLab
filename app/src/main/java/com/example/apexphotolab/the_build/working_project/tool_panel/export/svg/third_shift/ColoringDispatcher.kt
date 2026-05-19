package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.third_shift_essential.ThirdShiftHiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.third_shift.third_shift_essential.ThirdShiftPathSlicer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.VRAM_Garage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Job: Coloring Dispatcher.
 * Responsibility: Distributing path coloring work across Census Taker workers in parallel.
 */
object ColoringDispatcher {

    suspend fun resolveColorsInParallel(
        paths: List<List<Point>>,
        quantizedImage: Bitmap
    ): List<CensusReport> = withContext(Dispatchers.Default) {

        val hiredCrew = ThirdShiftHiringDepartment.hireCensusTakers()
        if (hiredCrew.isEmpty()) return@withContext emptyList()

        val workSlices = ThirdShiftPathSlicer.createSlices(paths)

        val jobs = workSlices.mapIndexed { index, slice ->
            val censusTaker = hiredCrew[index % hiredCrew.size].first
            val workstation = hiredCrew[index % hiredCrew.size].second

            async(workstation) {
                val vramSlot = VRAM_Garage.getSlotForManager(censusTaker.id)
                val results = mutableListOf<Pair<Int, CensusReport>>()
                slice.forEach { (originalIndex, pathData) ->
                    VRAM_Garage.wipeSlot(censusTaker.id)
                    val report = censusTaker.analyzePath(pathData, quantizedImage, vramSlot)
                    results.add(originalIndex to report)
                }
                results
            }
        }

        val unorderedResults = jobs.awaitAll().flatten()
        return@withContext unorderedResults.sortedBy { it.first }.map { it.second }
    }
}
