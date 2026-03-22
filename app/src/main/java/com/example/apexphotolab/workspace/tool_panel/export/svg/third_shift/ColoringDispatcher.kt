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
 * Clones the First Shift's successful "equal slices" model.
 */
object ColoringDispatcher {

    suspend fun resolveColorsInParallel(
        paths: List<List<Point>>,
        quantizedImage: Bitmap
    ): List<Int> = withContext(Dispatchers.Default) {

        val hiredCrew = ThirdShiftHiringDepartment.hireCensusTakers()
        if (hiredCrew.isEmpty()) return@withContext emptyList()

        val workSlices = ThirdShiftPathSlicer.createSlices(paths)

        val jobs = workSlices.mapIndexed { index, slice ->
            val censusTaker = hiredCrew[index % hiredCrew.size].first
            val workstation = hiredCrew[index % hiredCrew.size].second
            
            async(workstation) {
                val vramSlot = VRAM_Garage.getSlotForManager(censusTaker.id)
                val results = mutableListOf<Pair<Int, Int>>()
                slice.forEach { (originalIndex, pathData) ->
                    // POWER WASH: Wipe the slot before every shape analysis to prevent "Ghost Fills"
                    VRAM_Garage.wipeSlot(censusTaker.id)

                    val color = censusTaker.analyzePath(pathData, quantizedImage, vramSlot)
                    results.add(originalIndex to color)
                }
                results
            }
        }

        val unorderedResults = jobs.awaitAll().flatten()

        val orderedResults = unorderedResults.sortedBy { it.first }.map { it.second }
        return@withContext orderedResults
    }
}
