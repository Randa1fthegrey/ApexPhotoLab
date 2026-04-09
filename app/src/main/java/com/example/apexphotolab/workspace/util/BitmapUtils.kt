package com.example.apexphotolab.workspace.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream

object BitmapUtils {

    /**
     * Loads a bitmap from a URI and automatically corrects its orientation based on EXIF metadata.
     */
    fun decodeCorrectedBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val decoded = BitmapFactory.decodeStream(stream) ?: return null
                
                // Get a fresh stream for the EXIF reader
                context.contentResolver.openInputStream(uri)?.use { exifStream ->
                    applyExifOrientation(decoded, exifStream)
                } ?: decoded
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Reads EXIF metadata and applies correction (rotation/flipping) to the bitmap.
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
