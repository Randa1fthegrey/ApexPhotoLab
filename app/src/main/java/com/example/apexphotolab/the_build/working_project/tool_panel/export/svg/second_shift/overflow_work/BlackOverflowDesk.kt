package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.overflow_work

import android.graphics.Point
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Job: Black Overflow Desk.
 * Responsibility: Securely gathering traced Black path fragments from the entire swarm.
 */
object BlackOverflowDesk {

    private val accrualList = CopyOnWriteArrayList<List<Point>>()

    fun deposit(blobs: List<List<Point>>) {
        if (blobs.isEmpty()) return
        accrualList.addAll(blobs)
    }

    fun harvest(): List<List<Point>> {
        val results = accrualList.toList()
        return results
    }

    fun wipe() {
        accrualList.clear()
    }
}
