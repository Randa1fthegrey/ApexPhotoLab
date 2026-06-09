package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.gradient

import android.graphics.Bitmap
import android.graphics.Point

/**
 * Job: Gradient Deep Sampler.
 * Responsibility: Sampling pixel colors from within a shape to avoid edge noise or transparency leaks.
 */
object GradientDeepSampler {

    fun sample(p: Point, source: Bitmap, depth: Int): Int {
        val centerX = source.width / 2
        val centerY = source.height / 2
        var sx = p.x
        var sy = p.y

        // Push sample point toward the center of the image
        if (sx < centerX) sx += depth else sx -= depth
        if (sy < centerY) sy += depth else sy -= depth

        return source.getPixel(sx.coerceIn(0, source.width - 1), sy.coerceIn(0, source.height - 1))
    }
}