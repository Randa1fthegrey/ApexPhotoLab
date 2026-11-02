package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point

/**
 * Job: CVPS Job 2 Specialist - Set Pruner.
 * Responsibility: Synchronizing removed edge points with a bucket's candidate point set.
 */
object CVPS_job2_Pruner {

    fun prune(targetSet: MutableSet<Point>, removedPoints: Collection<Point>) {
        if (removedPoints.isEmpty() || targetSet.isEmpty()) return
        targetSet.removeAll(removedPoints.toSet())
    }
}
