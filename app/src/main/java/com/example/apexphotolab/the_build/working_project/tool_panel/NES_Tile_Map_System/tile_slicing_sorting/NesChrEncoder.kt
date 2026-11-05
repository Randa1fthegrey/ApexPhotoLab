package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting

/**
 * Job: NES 2-Bitplane CHR Encoder.
 * Responsibility: Encodes 8x8 pixel 2-bit color index tiles into the official NES 16-byte CHR format.
 * - Bytes 0..7: Low Bitplane
 * - Bytes 8..15: High Bitplane
 * - 256 tiles x 16 bytes = 4,096 Bytes CHR Table.
 */
object NesChrEncoder {

    fun encodeTile(colorIndexPixels: IntArray): ByteArray {
        val chrData = ByteArray(16)
        if (colorIndexPixels.size < 64) return chrData

        for (row in 0 until 8) {
            var lowByte = 0
            var highByte = 0

            for (col in 0 until 8) {
                val colorIndex = colorIndexPixels[row * 8 + col] and 0x03
                val bitPos = 7 - col

                val bit0 = colorIndex and 0x01
                val bit1 = (colorIndex shr 1) and 0x01

                lowByte = lowByte or (bit0 shl bitPos)
                highByte = highByte or (bit1 shl bitPos)
            }

            chrData[row] = lowByte.toByte()
            chrData[row + 8] = highByte.toByte()
        }

        return chrData
    }

    fun encodePatternTable(uniqueTilesColorIndices: List<IntArray>): ByteArray {
        val patternTable = ByteArray(4096)
        val tileCount = minOf(256, uniqueTilesColorIndices.size)

        for (i in 0 until tileCount) {
            val encodedTile = encodeTile(uniqueTilesColorIndices[i])
            System.arraycopy(encodedTile, 0, patternTable, i * 16, 16)
        }

        return patternTable
    }
}
