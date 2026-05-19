package com.example.apexphotolab.the_build.working_project.util.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.apexphotolab.the_build.working_project.util.bitmap.BitmapOrientationFixer

/**
 * Job: Resource Resolution.
 * Responsibility: Interacting with the Android ContentResolver to turn a Uri into a raw Bitmap.
 * Platform-dependent: Requires Context.
 */
object BitmapLoader {

    /**
     * Loads a bitmap from a URI and automatically corrects its orientation using BitmapOrientationFixer.
     */
    fun decodeCorrectedBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val decoded = BitmapFactory.decodeStream(stream) ?: return null

                // Get a fresh stream for the EXIF reader in the fixer
                context.contentResolver.openInputStream(uri)?.use { exifStream ->
                    BitmapOrientationFixer.applyExifOrientation(decoded, exifStream)
                } ?: decoded
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
