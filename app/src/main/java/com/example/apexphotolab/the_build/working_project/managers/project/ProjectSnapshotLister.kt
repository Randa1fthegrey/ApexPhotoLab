package com.example.apexphotolab.the_build.working_project.managers.project

import androidx.documentfile.provider.DocumentFile

/**
 * Job: Snapshot History Lister Specialist.
 * Responsibility: Retrieves and sorts history JSON snapshot files for a project directory.
 */
object ProjectSnapshotLister {

    fun getHistory(projectDir: DocumentFile): List<DocumentFile> {
        return projectDir.listFiles()
            .filter { it.name?.startsWith(val_util.PREFIX_SAVE) == true && it.name?.endsWith(val_util.EXT_JSON) == true }
            .sortedByDescending { it.lastModified() }
    }
}
