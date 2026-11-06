package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting

import java.util.Arrays

/**
 * Job: NES MMC5 Tile Sorter & Deduplicator.
 * Responsibility: Compares all 960 raw tiles and builds the CHR Pattern Table.
 * With the MMC5 mapper, the CHR ROM banking allows up to 16,384 background tiles,
 * so we safely allow all 960 tiles of the screen to be fully unique, eliminating
 * scrambled repetition at the bottom of the screen!
 */
object NesTileDeduplicator {

    data class DeduplicationResult(
        val uniqueTiles: List<IntArray>, // MMC5 allows up to 960 unique tiles
        val nametableTileIds: IntArray   // Using IntArray since tile IDs can exceed 255
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as DeduplicationResult
            if (uniqueTiles != other.uniqueTiles) return false
            if (!nametableTileIds.contentEquals(other.nametableTileIds)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = uniqueTiles.hashCode()
            result = 31 * result + nametableTileIds.contentHashCode()
            return result
        }
    }

    fun deduplicate(rawTiles: List<NesTileSlicer.RawTile>): DeduplicationResult {
        val uniqueTiles = mutableListOf<IntArray>()
        val nametableTileIds = IntArray(960)

        rawTiles.forEachIndexed { gridIndex, tile ->
            var matchedIndex = -1

            // Search for existing exact duplicate tile
            for (i in uniqueTiles.indices) {
                if (Arrays.equals(uniqueTiles[i], tile.pixels)) {
                    matchedIndex = i
                    break
                }
            }

            // If unique, add it (MMC5 easily handles 960 max limit)
            if (matchedIndex == -1) {
                matchedIndex = uniqueTiles.size
                uniqueTiles.add(tile.pixels)
            }

            if (gridIndex < 960) {
                nametableTileIds[gridIndex] = matchedIndex
            }
        }

        return DeduplicationResult(uniqueTiles, nametableTileIds)
    }
}
