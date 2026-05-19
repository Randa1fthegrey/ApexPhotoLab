package com.example.apexphotolab.the_build.working_project.editor

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.apexphotolab.the_build.working_project.util.layers.Layer

/**
 * Job: State Ownership (Layers).
 * A pure data container for the workspace's layer stack.
 */
class EditorModel {
    val layers: SnapshotStateList<Layer> = mutableStateListOf()
}