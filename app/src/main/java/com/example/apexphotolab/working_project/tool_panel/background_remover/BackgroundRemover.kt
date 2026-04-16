package com.example.apexphotolab.working_project.tool_panel.background_remover

import android.graphics.Bitmap
import android.graphics.Color
import com.example.apexphotolab.working_project.tool_panel.brush_logic.ColorMatcher
import java.util.BitSet

/**
 * Job: Execution Worker.
 * Responsibility: Performs destructive background removal on a bitmap.
 * 
 * Logic: HARD-BOUNDED SLOPE.
 * Uses a "Chain of Evidence" for gradients, but strictly forbids the color
 * from drifting too far from the provided anchorColor.
 * 
 * Purified: Guess-Blind. It doesn't know where the anchorColor came from.
 */
object BackgroundRemover {

    private const val GLOBAL_CEILING = 0.20f 
    private const val SLOPE_THRESHOLD = 0.022f 

    /**
     * Removes pixels matching the anchorColor using a flood-fill approach.
     * @return true if the operation was performed.
     */
    fun removeBackground(bitmap: Bitmap, anchorColor: Int): Boolean {
        if (!bitmap.isMutable) return false
        
        val width = bitmap.width
        val height = bitmap.height
        val anchor = anchorColor
        
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val visited = BitSet(width * height)
        val queue = IntArray(width * height)
        var head = 0
        var tail = 0
        
        // Seed the queue with all edge pixels that are "Likely Background"
        for (x in 0 until width) {
            val top = 0 * width + x
            if (ColorMatcher.isSimilar(pixels[top], anchor, 0.15f)) {
                visited.set(top); queue[tail++] = top
            }
            val bot = (height - 1) * width + x
            if (ColorMatcher.isSimilar(pixels[bot], anchor, 0.15f)) {
                visited.set(bot); queue[tail++] = bot
            }
        }
        for (y in 0 until height) {
            val left = y * width
            if (ColorMatcher.isSimilar(pixels[left], anchor, 0.15f)) {
                visited.set(left); queue[tail++] = left
            }
            val right = y * width + (width - 1)
            if (ColorMatcher.isSimilar(pixels[right], anchor, 0.15f)) {
                visited.set(right); queue[tail++] = right
            }
        }

        while (head < tail) {
            val idx = queue[head++]
            val x = idx % width
            val y = idx / width
            val currentColor = pixels[idx]
            
            pixels[idx] = Color.TRANSPARENT
            
            val neighbors = arrayOf(
                x to y - 1, x to y + 1, x - 1 to y, x + 1 to y
            )

            for ((nx, ny) in neighbors) {
                if (nx in 0 until width && ny in 0 until height) {
                    val nIdx = ny * width + nx
                    if (!visited.get(nIdx)) {
                        val neighborColor = pixels[nIdx]
                        
                        val fitsAnchor = ColorMatcher.isSimilar(neighborColor, anchor, GLOBAL_CEILING)
                        val fitsSlope = ColorMatcher.isSimilar(neighborColor, currentColor, SLOPE_THRESHOLD)

                        if (fitsAnchor && fitsSlope) {
                            visited.set(nIdx)
                            queue[tail++] = nIdx
                        }
                    }
                }
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return true
    }
}
