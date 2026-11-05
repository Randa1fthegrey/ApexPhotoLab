package com.example.apexphotolab.the_build.working_project.managers.project

import androidx.documentfile.provider.DocumentFile

/**
 * Job: Snapshot Renaming Specialist.
 * Responsibility: Renames snapshot DocumentFiles with new custom note annotations.
 */
object ProjectSnapshotRenamer {

    fun renameSnapshot(snapshot: DocumentFile, newNote: String): Boolean {
        val name = snapshot.name ?: return false
        val parts = name.split("_")
        if (parts.size < 2) return false
        val timestamp = parts[1]
        val newName = "${val_util.PREFIX_SAVE}${timestamp}_${newNote}${val_util.EXT_JSON}"
        return snapshot.renameTo(newName)
    }
}
