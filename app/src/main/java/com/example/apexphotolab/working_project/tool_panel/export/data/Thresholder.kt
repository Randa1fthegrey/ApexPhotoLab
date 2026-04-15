package com.example.apexphotolab.working_project.tool_panel.export.data

import android.graphics.Bitmap
import android.graphics.Color

object Thresholder {
    fun apply(input: Bitmap, threshold: Float = 0.5f): Bitmap {
        val width = input.width
        val height = input.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = input.getPixel(x, y)
                // A simple luminance calculation
                val luminance = (Color.red(pixel) * 0.2126 + Color.green(pixel) * 0.7152 + Color.blue(pixel) * 0.0722) / 255
                if (luminance > threshold) {
                    output.setPixel(x, y, Color.WHITE)
                } else {
                    output.setPixel(x, y, Color.BLACK)
                }
            }
        }
        return output
    }
}