package com.example.apexphotolab.working_project.tool_panel.export.svg.first_shift

import com.example.apexphotolab.working_project.tool_panel.export.svg.utils.VRAM_Garage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The "Factory Floor Dispatcher" for the First Shift (Color Quantization).
 * Its single job is to manage the conveyor belt and the active, VRAM-aware managers.
 */
object WorkDispatcher {

    suspend fun dispatch(
        workSlices: List<IntRange>,
        sourcePixels: IntArray,
        targetPixels: IntArray
    ) = withContext(Dispatchers.Default) {

        val hiredCrew = HiringDepartment.hireManagers()
        if (hiredCrew.isEmpty()) {
            return@withContext
        }

        val workQueue = Channel<IntRange>(Channel.Factory.UNLIMITED)

        launch {
            workSlices.forEach { workQueue.send(it) }
            workQueue.close()
        }

        val managerJobs = hiredCrew.map { (manager, workstation) ->
            launch(workstation) {
                val vramSlot = VRAM_Garage.getSlotForManager(manager.id)

                for (slice in workQueue) {
                    // Pass the private VRAM slot to the worker.
                    manager.processSlice(sourcePixels, targetPixels, slice, vramSlot)
                }
            }
        }

        managerJobs.joinAll()
    }
}