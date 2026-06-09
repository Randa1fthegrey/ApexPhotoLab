package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.vps_jobs

import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.dispatchers.TracingRouter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.swarm_mgmt.TracingSwarmManager
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure.SecondShiftResultManager
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.VRAM_Garage

/**
 * Job: VPS Job 2 - Path Tracing.
 * Responsibility: Executing the parallel work-stealing loop for path tracing.
 */
object VPS_job2 {

    suspend fun execute(workerId: Int, data: Any?) {
        TracingSwarmManager.registerAgent()
        try {
            while (true) {
                // Fetch next assignment from the master swarm manager
                val assignment = TracingSwarmManager.checkIn(workerId) ?: break

                // Prepare sandboxed memory slot
                val myVramSlot = workerId + 9
                val myVram = VRAM_Garage.getSlotForManager(myVramSlot)
                myVram.clear()
                myVram.put(assignment.vram.duplicate())
                myVram.flip()

                // Execute tracing via the router
                val blobs = TracingRouter.route(
                    assignment.colorIndex,
                    hashSetOf(),
                    myVram,
                    assignment.width,
                    assignment.height,
                    assignment.pixels,
                    assignment.sharedRemainingSet,
                    assignment.candidates
                )

                // Deposit results
                SecondShiftResultManager.deposit(assignment.colorIndex, blobs)
            }
        } finally {
            TracingSwarmManager.unregisterAgent()
        }
    }
}
