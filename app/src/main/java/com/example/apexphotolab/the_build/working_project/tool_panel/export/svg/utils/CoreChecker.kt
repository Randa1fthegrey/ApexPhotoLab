package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.utils

/**
 * Job: CPU Core Scout.
 * Responsibility: Reporting the number of available CPU cores.
 */
object CoreChecker {

    val coreCount: Int by lazy {
        Runtime.getRuntime().availableProcessors()
    }
}
