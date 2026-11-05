package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting

import java.util.Arrays

/**
 * Job: NES Tile Sorter & Deduplicator.
 * Responsibility: Compares all 960 raw tiles, extracts unique 8x8 tile patterns,
 * and compiles the 256-tile CHR Pattern Table and 960-byte Nametable Tile Map.
 */
object NesTileDeduplicator {

    data class DeduplicationResult(
        val uniqueTiles: List<IntArray>, // Up to 256 unique 8x8 tile pixel arrays
        val nametableTileIds: ByteArray // 960 tile IDs (0..255) for the 32x30 grid
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
        val nametableTileIds = ByteArray(960)

        rawTiles.forEachIndexed { gridIndex, tile ->
            var matchedIndex = -1

            for (i in uniqueTiles.indices) {
                if (Arrays.equals(uniqueTiles[i], tile.pixels)) {
                    matchedIndex = i
                    break
                }
            }

            if (matchedIndex == -1) {
                if (uniqueTiles.size < 256) {
                    matchedIndex = uniqueTiles.size
                    uniqueTiles.add(tile.pixels)
                } else {
                    // Fallback if unique count exceeds NES 256-tile CHR limit: map to nearest
                    matchedIndex = findNearestTileIndex(uniqueTiles, tile.pixels)
                }
            }

            if (gridIndex < 960) {
                nametableTileIds[gridIndex] = matchedIndex.toByte()
            }
        }

        return DeduplicationResult(uniqueTiles, nametableTileIds)
    }

    private fun findNearestTileIndex(uniqueTiles: List<IntArray>, targetPixels: IntArray): Int {
        var minDiff = Long.MAX_VALUE
        var bestIndex = 0

        uniqueTiles.forEachIndexed { index, candidate ->
            var diff = 0L
            for (i in 0 until 64) {
                val p1 = candidate[i]
                val p2 = targetPixels[i]
                val rDiff = ((p1 shr 16) and 0xFF) - ((p2 shr 16) and 0xFF)
                val gDiff = ((p1 shr 8) and 0xFF) - ((p2 shr 8) and 0xFF)
                val bDiff = (p1 and 0xFF) - (p2 and 0xFF)
                diff += (rDiff * rDiff + gDiff * gDiff + bDiff * bDiff)
            }
            if (diff < minDiff) {
                minDiff = diff
                bestIndex = index
            }
        }
        return bestIndex
    }
}
