package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.first_shift.territory

/**
 * Job: Color Wall Manager (The Orchestrator).
 * Responsibility: Serving as the single entry point for Map and Similarity queries,
 * delegating to specialized logic workers.
 */
object ColorWallScale {

    /**
     * Determines the Territory ID for a given pixel based on the 4D Map.
     */
    fun getTerritoryId(pixel: Int): Int {
        return ColorTerritoryMapper.map(pixel)
    }

    /**
     * The 99% Rule: Checks if two pixels are solid neighbors or if there is tension.
     */
    fun isSolidGround(pixelA: Int, pixelB: Int): Boolean {
        return ColorGeometricComparator.areSimilar(pixelA, pixelB)
    }
}