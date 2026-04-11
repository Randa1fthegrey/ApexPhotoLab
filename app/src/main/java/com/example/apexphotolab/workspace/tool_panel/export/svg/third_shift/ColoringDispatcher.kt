package com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift

import android.graphics.Bitmap
import android.graphics.Point
import com.example.apexphotolab.workspace.tool_panel.export.svg.utils.VRAM_Garage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * The "Factory Floor Dispatcher" for the Third Shift (Path Coloring).
 * Updated: Now returns a list of CensusReports to allow for Two-Desk Routing.
 */
object ColoringDispatcher {

    suspend fun resolveColorsInParallel(
        paths: List<List<Point>>,
        quantizedImage: Bitmap
    ): List<CensusReport> = withContext(Dispatchers.Default) {

        val hiredCrew = ThirdShiftHiringDepartment.hireCensusTakers()
        if (hiredCrew.isEmpty()) return@withContext emptyList()

        // Create slices of the path fragments
        val workSlices = ThirdShiftPathSlicer.createSlices(paths)

        val jobs = workSlices.mapIndexed { index, slice ->
            val censusTaker = hiredCrew[index % hiredCrew.size].first
            val workstation = hiredCrew[index % hiredCrew.size].second
            
            async(workstation) {
                val vramSlot = VRAM_Garage.getSlotForManager(censusTaker.id)
                val results = mutableListOf<Pair<Int, CensusReport>>()
                slice.forEach { (originalIndex, pathData) ->
                    VRAM_Garage.wipeSlot(censusTaker.id)
                    // Each census taker finds the statistical CensusReport for its fragment
                    val report = censusTaker.analyzePath(pathData, quantizedImage, vramSlot)
                    results.add(originalIndex to report)
                }
                results
            }
        }

        val unorderedResults = jobs.awaitAll().flatten()
        val orderedResults = unorderedResults.sortedBy { it.first }.map { it.second }

        return@withContext orderedResults
    }
}
