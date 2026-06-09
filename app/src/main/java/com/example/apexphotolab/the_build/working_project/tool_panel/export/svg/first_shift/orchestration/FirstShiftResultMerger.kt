package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.orchestration

/**
 * Job: First Shift Result Merger.
 * Responsibility: Merging local color buckets from multiple parallel workers into a single unified 10-bucket list.
 */
object FirstShiftResultMerger {

    /**
     * Combines the bucket lists from all parallel workers.
     * Expects a list of 10-bucket results (one from each worker).
     */
    fun merge(allResults: List<List<List<Int>>>): List<List<Int>> {
        val finalBuckets = List(10) { mutableListOf<Int>() }
        for (workerResult in allResults) {
            for (i in 0 until 10) {
                finalBuckets[i].addAll(workerResult[i])
            }
        }
        return finalBuckets
    }
}