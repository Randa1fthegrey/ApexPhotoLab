package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.VRAM_Garage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Job: Work Dispatcher.
 * Responsibility: Managing the work queue and merging pixel buckets from parallel first shift workers.
 */
object WorkDispatcher {

    suspend fun dispatch(
        workSlices: List<IntRange>,
        sourcePixels: IntArray,
        targetPixels: IntArray
    ): List<List<Int>> = withContext(Dispatchers.Default) {

        val hiredCrew = HiringDepartment.hireWorkers()
        if (hiredCrew.isEmpty()) {
            return@withContext emptyList()
        }

        val workQueue = Channel<IntRange>(Channel.Factory.UNLIMITED)

        launch {
            workSlices.forEach { workQueue.send(it) }
            workQueue.close()
        }

        val workerJobs = hiredCrew.map { (worker, workstation) ->
            async(workstation) {
                val vramSlot = VRAM_Garage.getSlotForManager(worker.id)
                val localBuckets = List(10) { mutableListOf<Int>() }

                for (slice in workQueue) {
                    val result = worker.processSlice(sourcePixels, targetPixels, slice, vramSlot)
                    for (i in 0 until 10) {
                        localBuckets[i].addAll(result[i])
                    }
                }
                localBuckets
            }
        }

        val allResults = workerJobs.awaitAll()
        
        // Merge the buckets from all workers into a single 10-bucket list
        val finalBuckets = List(10) { mutableListOf<Int>() }
        for (workerResult in allResults) {
            for (i in 0 until 10) {
                finalBuckets[i].addAll(workerResult[i])
            }
        }
        
        return@withContext finalBuckets
    }
}
