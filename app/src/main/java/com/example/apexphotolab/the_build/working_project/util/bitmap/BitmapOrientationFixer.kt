package com.example.apexphotolab.the_build.working_project.util.bitmap

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream

/**
 * Job: Coordinate Correction.
 * Responsibility: Inspecting EXIF metadata and applying Matrix transformations to Bitmaps.
 * Purified: Focuses on the "Fix" logic.
 */
object BitmapOrientationFixer {

    /**
     * Reads EXIF metadata from the stream and applies correction (rotation/flipping) to the bitmap.
     */
    fun applyExifOrientation(bitmap: Bitmap, inputStream: InputStream): Bitmap {
        val exif = try {
            ExifInterface(inputStream)
        } catch (e: Exception) {
            return bitmap
        }

        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postRotate(270f)
                matrix.postScale(-1f, 1f)
            }
            else -> return bitmap // No correction needed
        }

        return try {
            val corrected = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
            if (corrected != bitmap) {
                bitmap.recycle() // Clean up original if we created a new one
            }
            corrected
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap
        }
    }
}