package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting

import android.graphics.Bitmap
import android.graphics.Color

/**
 * Job: 13-Color Background Pipeline Specialist.
 * Responsibility: Scans a high-res image, extracts the Universal Background color + up to 12 Accents,
 * and funnels every pixel into one of those 13 strict NES color buckets.
 */
object NesBackgroundQuantizer {

    // Official 64-Color NES NTSC Hardware Palette (ARGB Values)
    val NES_MASTER_PALETTE = intArrayOf(
        0xFF666666.toInt(), 0xFF002A88.toInt(), 0xFF1412A7.toInt(), 0xFF3B00A4.toInt(),
        0xFF5C007E.toInt(), 0xFF6D0040.toInt(), 0xFF6B0006.toInt(), 0xFF560A00.toInt(),
        0xFF332C00.toInt(), 0xFF0B4200.toInt(), 0xFF005100.toInt(), 0xFF00472E.toInt(),
        0xFF003D65.toInt(), 0xFF000000.toInt(), 0xFF000000.toInt(), 0xFF000000.toInt(),
        0xFFADADAD.toInt(), 0xFF155FD9.toInt(), 0xFF4240FF.toInt(), 0xFF7527FE.toInt(),
        0xFFA01ACC.toInt(), 0xFFB71E7B.toInt(), 0xFFB53120.toInt(), 0xFF994E00.toInt(),
        0xFF6B6D00.toInt(), 0xFF388700.toInt(), 0xFF0C9300.toInt(), 0xFF008F32.toInt(),
        0xFF007C8D.toInt(), 0xFF000000.toInt(), 0xFF000000.toInt(), 0xFF000000.toInt(),
        0xFFFFFEFF.toInt(), 0xFF64B0FF.toInt(), 0xFF9290FF.toInt(), 0xFFC676FF.toInt(),
        0xFFF36AFF.toInt(), 0xFFFE6EC0.toInt(), 0xFFFE8170.toInt(), 0xFFEA9E22.toInt(),
        0xFFBCBE00.toInt(), 0xFF88D800.toInt(), 0xFF5CE430.toInt(), 0xFF45E082.toInt(),
        0xFF48CDDE.toInt(), 0xFF4F4F4F.toInt(), 0xFF000000.toInt(), 0xFF000000.toInt(),
        0xFFFFFEFF.toInt(), 0xFFC0E1FF.toInt(), 0xFFD3D2FF.toInt(), 0xFFE8C5FF.toInt(),
        0xFFFBC2FF.toInt(), 0xFFFEC2EA.toInt(), 0xFFFECAFA.toInt(), 0xFFF9D700.toInt(),
        0xFFE6E59A.toInt(), 0xFFD2F09A.toInt(), 0xFFB8F5B8.toInt(), 0xFFB0F3DA.toInt(),
        0xFFB2ECE6.toInt(), 0xFFB8B8B8.toInt(), 0xFF000000.toInt(), 0xFF000000.toInt()
    )

    data class Quantized13Result(
        val bitmap13Color: Bitmap,
        val active13Colors: IntArray // Index 0 = Universal BG. Indexes 1-12 = Accents.
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Quantized13Result
            if (bitmap13Color != other.bitmap13Color) return false
            if (!active13Colors.contentEquals(other.active13Colors)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = bitmap13Color.hashCode()
            result = 31 * result + active13Colors.contentHashCode()
            return result
        }
    }

    fun quantizeTo13Colors(bitmap: Bitmap): Quantized13Result {
        val width = bitmap.width
        val height = bitmap.height

        val rawPixels = IntArray(width * height)
        bitmap.getPixels(rawPixels, 0, width, 0, 0, width, height)

        // 1. Initial pass: Snap to 64-color NES Master Palette to eliminate JPEG noise
        val colorCounts = mutableMapOf<Int, Int>()
        val snappedPixels = IntArray(rawPixels.size)

        for (i in rawPixels.indices) {
            val nearest = findNearestColor(rawPixels[i], NES_MASTER_PALETTE)
            snappedPixels[i] = nearest
            colorCounts[nearest] = (colorCounts[nearest] ?: 0) + 1
        }

        // 2. Extract Top 13 Colors
        val sortedColors = colorCounts.entries.sortedByDescending { it.value }.map { it.key }

        val active13 = IntArray(13)
        // Index 0 is the Universal Background (The most dominant color in the image)
        active13[0] = if (sortedColors.isNotEmpty()) sortedColors[0] else NES_MASTER_PALETTE[13]

        // Indexes 1-12 are the Accents
        var accentCount = 1
        for (i in 1 until sortedColors.size) {
            if (accentCount >= 13) break

            val candidate = sortedColors[i]
            
            // Stricter distinctness check: if a color is too close to an existing color,
            // we skip it to preserve slots for vibrant colors (like Gold and Cyan).
            var isDistinct = true
            for (j in 0 until accentCount) {
                // Increased distance threshold from 30.0 to 45.0 to force broader color variety
                if (colorDistance(candidate, active13[j]) < 45.0) {
                    isDistinct = false
                    break
                }
            }

            if (isDistinct) {
                active13[accentCount] = candidate
                accentCount++
            }
        }

        // Fill any empty accent slots with background if we didn't reach 13
        while (accentCount < 13) {
            active13[accentCount] = active13[0]
            accentCount++
        }

        // 3. The Funnel: Snap every high-res pixel strictly into one of the 13 buckets
        val finalPixels = IntArray(rawPixels.size)
        for (i in snappedPixels.indices) {
            finalPixels[i] = findNearestColor(snappedPixels[i], active13)
        }

        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        outputBitmap.setPixels(finalPixels, 0, width, 0, 0, width, height)

        return Quantized13Result(outputBitmap, active13)
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
            val lumaPenalty = Math.abs(targetLuma - luma) * 3.0

            val score = dist + lumaPenalty

            if (score < minScore) {
                minScore = score
                bestColor = color
            }
        }
        return bestColor
    }

    private fun colorDistance(c1: Int, c2: Int): Double {
        val r = Color.red(c1) - Color.red(c2)
        val g = Color.green(c1) - Color.green(c2)
        val b = Color.blue(c1) - Color.blue(c2)
        return Math.sqrt((r * r + g * g + b * b).toDouble())
    }
}
