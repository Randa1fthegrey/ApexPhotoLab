package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.overflow_work.swarm_agents
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.TracingRouter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.overflow_work.OverflowRouter
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.overflow_work.TracingSwarmManager
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils.VRAM_Garage
object SwarmAgent5 : SwarmAgent {
    override val id = 5
    override suspend fun runSwarm() {
        while (true) {
            val assignment = TracingSwarmManager.checkIn(id) ?: break
            val myVram = VRAM_Garage.getSlotForManager(id)
            myVram.clear()
            myVram.put(assignment.vram.duplicate())
            myVram.flip()
            val blobs = TracingRouter.route(assignment.colorIndex, hashSetOf(), myVram, assignment.width, assignment.height, assignment.pixels, assignment.sharedRemainingSet, assignment.candidates)
            OverflowRouter.deposit(assignment.colorIndex, blobs)
        }
    }
}
