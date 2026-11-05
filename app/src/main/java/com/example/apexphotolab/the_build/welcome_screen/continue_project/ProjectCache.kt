package com.example.apexphotolab.the_build.welcome_screen.continue_project

/**
 * Job: Memory Cache Specialist.
 * Responsibility: Caches project directory lists in memory so the Continue Project screen
 * renders existing project items on Frame 1 (0.00ms) without screen pop-in or flash.
 */
object ProjectCache {
    var cachedCategories: ProjectFolderClassifier.Categories? = null

    fun update(categories: ProjectFolderClassifier.Categories) {
        cachedCategories = categories
    }

    fun clear() {
        cachedCategories = null
    }
}
