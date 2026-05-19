package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.overflow_work

import android.graphics.Point

/**
 * Job: Overflow Router.
 * Responsibility: Routing blobs to their correct color-specific overflow desk.
 */
object OverflowRouter {

    fun deposit(index: Int, blobs: List<List<Point>>) {
        when (index) {
            0 -> RedOverflowDesk.deposit(blobs)
            1 -> GreenOverflowDesk.deposit(blobs)
            2 -> BlueOverflowDesk.deposit(blobs)
            3 -> YellowOverflowDesk.deposit(blobs)
            4 -> CyanOverflowDesk.deposit(blobs)
            5 -> MagentaOverflowDesk.deposit(blobs)
            6 -> WhiteOverflowDesk.deposit(blobs)
            7 -> AlphaOverflowDesk.deposit(blobs)
            8 -> BlackOverflowDesk.deposit(blobs)
            9 -> GreyOverflowDesk.deposit(blobs)
        }
    }

    fun harvest(index: Int): List<List<Point>> {
        return when (index) {
            0 -> RedOverflowDesk.harvest()
            1 -> GreenOverflowDesk.harvest()
            2 -> BlueOverflowDesk.harvest()
            3 -> YellowOverflowDesk.harvest()
            4 -> CyanOverflowDesk.harvest()
            5 -> MagentaOverflowDesk.harvest()
            6 -> WhiteOverflowDesk.harvest()
            7 -> AlphaOverflowDesk.harvest()
            8 -> BlackOverflowDesk.harvest()
            9 -> GreyOverflowDesk.harvest()
            else -> emptyList()
        }
    }

    fun wipeAll() {
        RedOverflowDesk.wipe()
        GreenOverflowDesk.wipe()
        BlueOverflowDesk.wipe()
        YellowOverflowDesk.wipe()
        CyanOverflowDesk.wipe()
        MagentaOverflowDesk.wipe()
        WhiteOverflowDesk.wipe()
        AlphaOverflowDesk.wipe()
        BlackOverflowDesk.wipe()
        GreyOverflowDesk.wipe()
    }
}
