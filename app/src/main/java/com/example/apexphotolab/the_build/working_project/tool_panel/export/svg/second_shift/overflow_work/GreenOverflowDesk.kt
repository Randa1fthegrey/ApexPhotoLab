package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.overflow_work

import android.graphics.Point
import java.util.concurrent.CopyOnWriteArrayList

object GreenOverflowDesk {
    private val accrualList = CopyOnWriteArrayList<List<Point>>()
    fun deposit(blobs: List<List<Point>>) { if (blobs.isNotEmpty()) accrualList.addAll(blobs) }
    fun harvest(): List<List<Point>> = accrualList.toList()
    fun wipe() { accrualList.clear() }
}
