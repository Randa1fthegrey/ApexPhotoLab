package com.example.apexphotolab.workspace.toolbars.export.svg.utils

/**
 * The "Scout" for our custom dispatch engine.
 * Its one and only job is to report the number of available CPU cores.
 */
object CoreChecker {

    val coreCount: Int by lazy {
        // This will be calculated once and then cached for performance.
        Runtime.getRuntime().availableProcessors()
    }
}