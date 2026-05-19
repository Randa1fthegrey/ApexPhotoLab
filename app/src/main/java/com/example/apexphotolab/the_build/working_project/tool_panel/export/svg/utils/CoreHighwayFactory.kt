package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * Job: Core Highway Factory.
 * Responsibility: Building a dedicated single-thread coroutine dispatcher for each available CPU core.
 */
object CoreHighwayFactory {

    val coreHighways: List<CoroutineDispatcher> by lazy {
        val coreCount = CoreChecker.coreCount
        (0 until coreCount).map {
            Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        }
    }
}
