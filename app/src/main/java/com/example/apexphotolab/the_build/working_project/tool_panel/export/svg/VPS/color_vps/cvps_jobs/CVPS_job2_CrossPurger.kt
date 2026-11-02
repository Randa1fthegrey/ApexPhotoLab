package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.VPS.color_vps.cvps_jobs

import android.graphics.Point

/**
 * Job: CVPS Job 2 Specialist - Cross Purger.
 * Responsibility: Deconflicting claimed points across different color bucket candidate lists.
 */
object CVPS_job2_CrossPurger {

    fun purgeClaimed(bucketCandidates: List<MutableSet<Point>>, claimedGroupIndex: Int, claimedPoints: Collection<Point>) {
        if (claimedPoints.isEmpty()) return
        val claimedSet = claimedPoints.toSet()

        bucketCandidates.forEachIndexed { index, candidateSet ->
            if (index != claimedGroupIndex) {
                candidateSet.removeAll(claimedSet)
            }
        }
    }
}
