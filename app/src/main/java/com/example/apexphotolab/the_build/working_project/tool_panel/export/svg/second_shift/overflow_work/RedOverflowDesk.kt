package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.overflow_work

import android.graphics.Point
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Job: Red Overflow Desk.
 * Responsibility: Securely gathering traced Red path fragments from the entire swarm.
 */
object RedOverflowDesk {

    private val accrualList = CopyOnWriteArrayList<List<Point>>()

    fun deposit(blobs: List<List<Point>>) {
        if (blobs.isEmpty()) return
        accrualList.addAll(blobs)
    }

    fun harvest(): List<List<Point>> {
        return accrualList.toList()
    }

    fun wipe() {
        accrualList.clear()
    }
}
