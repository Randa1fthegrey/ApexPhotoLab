package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.flattening

import android.graphics.Bitmap

/**
 * Job: Aspect Ratio Crop Specialist.
 * Responsibility: Center-crops any input image to a perfect 4:3 aspect ratio (NES NTSC standard).
 * This ensures the image will scale down to 256x240 without squishing pixels or adding black letterbox padding.
 */
object NesAspectCropper {

    fun cropToNesAspect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val targetRatio = 256f / 240f // 4:3 (approx 1.066)
        val currentRatio = width.toFloat() / height.toFloat()

        var cropWidth = width
        var cropHeight = height
        var cropX = 0
        var cropY = 0

        if (currentRatio > targetRatio) {
            // Image is too wide (e.g. 16:9). Crop left and right sides.
            cropWidth = (height * targetRatio).toInt()
            cropX = (width - cropWidth) / 2
        } else if (currentRatio < targetRatio) {
            // Image is too tall. Crop top and bottom.
            cropHeight = (width / targetRatio).toInt()
            cropY = (height - cropHeight) / 2
        } else {
            // Aspect ratio is already perfect
            return bitmap
        }

        return Bitmap.createBitmap(bitmap, cropX, cropY, cropWidth, cropHeight)
    }
}
