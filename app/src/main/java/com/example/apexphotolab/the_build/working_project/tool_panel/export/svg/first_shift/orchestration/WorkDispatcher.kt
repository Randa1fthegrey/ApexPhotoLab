package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.orchestration

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.VPS_HiringDepartment
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.vps_jobs.VPS_job1
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools.VPS_Audit
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.VRAM_Garage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Job: Work Dispatcher.
 * Responsibility: Orchestrating the parallel quantization work queue and delegating result merging to the Merger.
 */
object WorkDispatcher {

    suspend fun dispatch(
        workSlices: List<IntRange>,
        sourcePixels: IntArray,
        targetPixels: IntArray
    ): List<List<Int>> = withContext(Dispatchers.Default) {

        VPS_Audit.logSystemOnline()

        val hiredCrew = VPS_HiringDepartment.hireWorkers()

        if (hiredCrew.isEmpty()) {
            return@withContext emptyList()
        }

        VPS_Audit.logShiftHandoff(1)

        val workQueue = Channel<IntRange>(Channel.UNLIMITED)

        launch {
            workSlices.forEach { workQueue.send(it) }
            workQueue.close()
        }

        val workerJobs = hiredCrew.map { (worker, workstation) ->
            async(workstation) {
                val vramSlot = VRAM_Garage.getSlotForManager(worker.id)
                val localBuckets = List(10) { mutableListOf<Int>() }

                for (slice in workQueue) {
                    VPS_Audit.logCompute(1, worker.id)

                    val taskData = VPS_job1.QuantizationData(
                        sourcePixels,
                        targetPixels,
                        slice,
                        vramSlot,
                        localBuckets
                    )
                    worker.runTask(1, taskData)
                }
                localBuckets
            }
        }

        val allResults = workerJobs.awaitAll()

        return@withContext FirstShiftResultMerger.merge(allResults)
    }
}