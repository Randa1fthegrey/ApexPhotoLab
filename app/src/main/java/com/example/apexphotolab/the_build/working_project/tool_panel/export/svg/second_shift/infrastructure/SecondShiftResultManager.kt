package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.infrastructure

import android.graphics.Point
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Job: Second Shift Result Manager.
 * Responsibility: Securely gathering traced path fragments from the parallel swarm
 * and providing a unified collection interface for harvesting.
 */
object SecondShiftResultManager {

    private val accrualMap = ConcurrentHashMap<Int, CopyOnWriteArrayList<List<Point>>>()

    fun deposit(index: Int, blobs: List<List<Point>>) {
        if (blobs.isEmpty()) return
        accrualMap.getOrPut(index) { CopyOnWriteArrayList() }.addAll(blobs)
    }

    fun harvest(index: Int): List<List<Point>> {
        return accrualMap[index]?.toList() ?: emptyList()
    }

    fun wipeAll() {
        accrualMap.clear()
    }
}