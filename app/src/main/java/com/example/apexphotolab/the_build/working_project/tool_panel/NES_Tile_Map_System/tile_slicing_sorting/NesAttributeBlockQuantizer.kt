package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting

import android.graphics.Bitmap
import android.graphics.Color

/**
 * Job: 8x8 MMC5 EXRAM Attribute Block Quantizer.
 * Responsibility: Simulates the Nintendo MMC5 (ExRAM) hardware capability.
 * Instead of standard 16x16 attribute blocks, MMC5 allows a unique 4-color sub-palette
 * for EVERY SINGLE 8x8 tile. This prevents chunky mosaic blocking and allows maximum
 * high-fidelity color detailing per tile!
 */
object NesAttributeBlockQuantizer {

    fun enforceAttributeBlocks(bitmap: Bitmap, universalBgColor: Int, master13Palette: IntArray): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val rawPixels = IntArray(width * height)
        bitmap.getPixels(rawPixels, 0, width, 0, 0, width, height)

        val finalPixels = IntArray(rawPixels.size)

        // Process image in 8x8 pixel blocks (MMC5 ExRAM mode)
        val cols = width / 8
        val rows = height / 8

        for (blockY in 0 until rows) {
            for (blockX in 0 until cols) {

                // 1. Gather color counts for this specific 8x8 block
                val colorCounts = mutableMapOf<Int, Int>()
                for (y in 0 until 8) {
                    for (x in 0 until 8) {
                        val pxX = blockX * 8 + x
                        val pxY = blockY * 8 + y
                        if (pxX < width && pxY < height) {
                            val pixel = rawPixels[pxY * width + pxX]
                            if (pixel != universalBgColor) {
                                colorCounts[pixel] = (colorCounts[pixel] ?: 0) + 1
                            }
                        }
                    }
                }

                // 2. Select the top 3 accent colors for this 8x8 block (Prioritizing high-contrast details)
                val blockAccents = colorCounts.entries
                    .sortedWith(Comparator { a, b ->
                        // Blend pixel count with brightness contrast to ensure small bright details (like text) survive
                        val aLuma = getLuminance(a.key)
                        val bLuma = getLuminance(b.key)
                        val bgLuma = getLuminance(universalBgColor)
                        
                        val aContrast = Math.abs(aLuma - bgLuma)
                        val bContrast = Math.abs(bLuma - bgLuma)
                        
                        val aScore = a.value * (1.0 + aContrast * 2.0)
                        val bScore = b.value * (1.0 + bContrast * 2.0)
                        
                        bScore.compareTo(aScore)
                    })
                    .map { it.key }
                    .take(3)
                    .toIntArray()

                // Pad block palette with Universal BG to ensure 4 colors max
                val blockPalette = IntArray(4)
                blockPalette[0] = universalBgColor
                for (i in 0 until 3) {
                    blockPalette[i + 1] = if (i < blockAccents.size) blockAccents[i] else universalBgColor
                }

                // 3. Ensure this 4-color sub-palette uses the optimal available colors from the master 13 list
                for (i in 1..3) {
                    var bestMatch = master13Palette[0]
                    var minDiff = Double.MAX_VALUE
                    for (mc in master13Palette) {
                        val d = colorDistance(blockPalette[i], mc)
                        if (d < minDiff) {
                            minDiff = d
                            bestMatch = mc
                        }
                    }
                    blockPalette[i] = bestMatch
                }

                // 4. Snap every pixel in the 8x8 block strictly to this 4-color Sub-Palette
                for (y in 0 until 8) {
                    for (x in 0 until 8) {
                        val pxX = blockX * 8 + x
                        val pxY = blockY * 8 + y
                        if (pxX < width && pxY < height) {
                            val idx = pxY * width + pxX
                            finalPixels[idx] = findNearestColor(rawPixels[idx], blockPalette)
                        }
                    }
                }
            }
        }

        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        outputBitmap.setPixels(finalPixels, 0, width, 0, 0, width, height)
        return outputBitmap
    }

    private fun colorDistance(c1: Int, c2: Int): Double {
        val r = Color.red(c1) - Color.red(c2)
        val g = Color.green(c1) - Color.green(c2)
        val b = Color.blue(c1) - Color.blue(c2)
        return Math.sqrt((r * r + g * g + b * b).toDouble())
    }

    private fun getLuminance(color: Int): Double {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun findNearestColor(targetColor: Int, palette: IntArray): Int {
        val targetR = Color.red(targetColor)
        val targetG = Color.green(targetColor)
        val targetB = Color.blue(targetColor)

        // Luma preserves brightness mapping, preventing bright edges from sinking into dark backgrounds
        val targetLuma = 0.2126 * targetR + 0.7152 * targetG + 0.0722 * targetB

        var minScore = Double.MAX_VALUE
        var bestColor = palette[0]

        for (color in palette) {
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            val dist = (targetR - r) * (targetR - r) +
                       (targetG - g) * (targetG - g) +
                       (targetB - b) * (targetB - b).toDouble()

            val luma = 0.2126 * r + 0.7152 * g + 0.0722 * b
            
            // Penalty for mismatching brightness
            val lumaPenalty = Math.abs(targetLuma - luma) * 3.0

            val score = dist + lumaPenalty

            if (score < minScore) {
                minScore = score
                bestColor = color
            }
        }
        return bestColor
    }
}
