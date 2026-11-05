package com.example.apexphotolab.the_build.working_project.managers.project

import androidx.documentfile.provider.DocumentFile

/**
 * Job: Snapshot Bulk Deletion Specialist.
 * Responsibility: Deletes specified snapshot DocumentFiles from storage.
 */
object ProjectSnapshotDeleter {

    fun deleteSnapshots(snapshots: List<DocumentFile>): Boolean {
        var allDeleted = true
        snapshots.forEach {
            if (!it.delete()) allDeleted = false
        }
        return allDeleted
    }
}
