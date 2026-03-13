package com.example.apexphotolab.workspace.toolbars.export.svg.svg_assembly

import android.graphics.Color
import android.graphics.Point

/**
 * A specialist for the new Assembly Shift.
 * Groups path fragments by their primary color family.
 * Robust Version: Sync'd Alpha Threshold to prevent "Alpha Capture" of colored shapes.
 */
object FragmentGrouper {

    data class GroupedFragments(
        val fragments: List<List<Point>>,
        val originalColors: List<Int>
    )

    private const val ALPHA_THRESHOLD = 100 // Sync'd with other Sorters

    fun group(
        closedPaths: List<List<Point>>,
        pathColors: List<Int>
    ): Map<Int, GroupedFragments> {
        val groups = mutableMapOf<Int, MutableList<Pair<List<Point>, Int>>>()

        pathColors.forEachIndexed { index, color ->
            // Skip fully transparent fragments.
            if (Color.alpha(color) > 0) {
                val groupIndex = getGroupIndexForColor(color)
                val path = closedPaths[index]
                groups.getOrPut(groupIndex) { mutableListOf() }.add(Pair(path, color))
            }
        }

        return groups.mapValues { (_, pairList) ->
            val fragments = pairList.map { it.first }
            val colors = pairList.map { it.second }
            GroupedFragments(fragments, colors)
        }
    }

    private fun getGroupIndexForColor(pixel: Int): Int {
        val a = Color.alpha(pixel)
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)

        val hsv = FloatArray(3)
        Color.RGBToHSV(r, g, b, hsv)
        val hue = hsv[0]
        val sat = hsv[1]
        val val_ = hsv[2]

        // 1. Alpha Check - Use sync'd threshold
        if (a < ALPHA_THRESHOLD) return 7

        // 2. Neutral Check (Synced with ColorGroupSorter)
        if (sat < 0.15f) {
            return when {
                val_ > 0.9f -> 6 // White
                val_ < 0.15f -> 8 // Black
                else -> 9 // Grey
            }
        }

        // 3. Opaque Color Check (Synced with ColorGroupSorter)
        return when (hue) {
            in 0f..20f, in 335f..360f -> 0 // Red
            in 20f..75f -> 3               // Yellow
            in 75f..160f -> 1              // Green
            in 160f..195f -> 4             // Cyan
            in 195f..265f -> 2             // Blue
            in 265f..335f -> 5             // Magenta
            else -> 9                      // Fallback
        }
    }
}
