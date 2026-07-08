package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point
import java.nio.ByteBuffer

/**
 * Job: CVPS Job 2 Specialist - Scout.
 * Responsibility: Identifying initial path fragments and detecting "Potholes" (gaps) for the repair crew.
 */
object CVPS_job2_Scout {

    fun execute(
        candidates: List<Point>,
        vram: ByteBuffer,
        width: Int,
        height: Int,
        pixels: IntArray,
        colorId: Int
    ): List<List<Point>> {
        val fragments = mutableListOf<List<Point>>()
        val remaining = candidates.toMutableSet()

        for (home in candidates) {
            if (!remaining.contains(home)) continue
            val currentPath = mutableListOf<Point>()
            var current = home
            var currentDir = 0

            while (true) {
                if (remaining.remove(current)) currentPath.add(current)
                
                // Use the core navigator
                val step = CVPS_job2_Tracer.findNextStep(current, currentDir, remaining, home, currentPath.size, vram, width, height, pixels, colorId)
                
                if (step.first != null) {
                    current = step.first!!
                    currentDir = step.second
                    if (current == home) break
                } else break
            }
            if (currentPath.size > 2) fragments.add(currentPath)
        }
        return fragments
    }
}
