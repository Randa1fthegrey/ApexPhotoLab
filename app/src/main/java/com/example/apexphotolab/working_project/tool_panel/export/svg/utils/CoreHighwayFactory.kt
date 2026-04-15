package com.example.apexphotolab.working_project.tool_panel.export.svg.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * The "Core Highway Factory".
 * Its one job is to build a dedicated, private "highway" (CoroutineDispatcher) for each available CPU core.
 * This guarantees that each manager runs on their own separate thread, ensuring true parallelism.
 */
object CoreHighwayFactory {

    // A list of private, single-lane highways, one for each CPU core.
    // It's lazy, so it's only built once when first accessed.
    val coreHighways: List<CoroutineDispatcher> by lazy {
        val coreCount = CoreChecker.coreCount
        (0 until coreCount).map {
            // For each core, build a new, private, single-lane highway.
            Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        }
    }
}
