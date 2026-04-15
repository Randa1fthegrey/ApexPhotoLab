package com.example.apexphotolab.working_project.tool_panel.eraser_bkgdremover.background_remover

import android.graphics.Bitmap
import android.graphics.Color
import com.example.apexphotolab.working_project.tool_panel.eraser_bkgdremover.ColorMatcher

/**
 * Automated Background Removal Tool.
 * 
 * Version: HARD-BOUNDED SLOPE.
 * Uses a "Chain of Evidence" for gradients, but strictly forbids the color
 * from drifting too far from the original background sample.
 */
object BackgroundRemover {

    // The Global Ceiling: No pixel can ever be erased if it's more than this 
    // far from the original corner color. This protects the subject.
    private const val GLOBAL_CEILING = 0.20f 
    
    // The Slope: How much change we allow between neighbors to follow gradients.
    private const val SLOPE_THRESHOLD = 0.022f 

    fun removeBackground(bitmap: Bitmap): Boolean {
        if (!bitmap.isMutable) return false
        
        val width = bitmap.width
        val height = bitmap.height
        
        // 1. Prepare for Scan
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // 2. Sample the 4 corners
        val corners = listOf(
            pixels[0],
            pixels[width - 1],
            pixels[(height - 1) * width],
            pixels[width * height - 1]
        )
        
        // 3. Identify the target background color
        var targetColor: Int? = null
        for (i in corners.indices) {
            var matchCount = 0
            for (j in corners.indices) {
                if (ColorMatcher.isSimilar(corners[i], corners[j], 0.12f)) {
                    matchCount++
                }
            }
            if (matchCount >= 2) {
                targetColor = corners[i]
                break
            }
        }

        if (targetColor == null) return false
        val anchor = targetColor // The "Ground Truth" color

        // 4. Execute Flood Fill
        val visited = java.util.BitSet(width * height)
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
            
            // Mark as removed
            pixels[idx] = Color.TRANSPARENT
            
            // Check neighbors: Up, Down, Left, Right
            val neighbors = arrayOf(
                x to y - 1, x to y + 1, x - 1 to y, x + 1 to y
            )

            for ((nx, ny) in neighbors) {
                if (nx in 0 until width && ny in 0 until height) {
                    val nIdx = ny * width + nx
                    if (!visited.get(nIdx)) {
                        val neighborColor = pixels[nIdx]

                        // HARD-BOUNDED SLOPE LOGIC:
                        // 1. Must be within the Global Ceiling of the ORIGINAL color (The Anchor).
                        // 2. Must be similar to the CURRENT neighbor (The Slope).
                        
                        val fitsAnchor = ColorMatcher.isSimilar(neighborColor, anchor, GLOBAL_CEILING)
                        val fitsSlope = ColorMatcher.isSimilar(neighborColor, currentColor, SLOPE_THRESHOLD)

                        // If it fits both, we continue the flow.
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
