package com.example.apexphotolab.workspace.tool_panel.export.data

import android.content.Context
import android.graphics.Bitmap
import com.example.apexphotolab.workspace.tool_panel.layers.Layer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * A manual BMP (Windows Bitmap) writer.
 * Implements a 32-bit BGRA Uncompressed BMP (V3 BITMAPINFOHEADER).
 * Follows the Microsoft BMP File Format Specification.
 */
object BmpSaver {

    suspend fun saveFlattenedBmp(
        context: Context,
        layers: List<Layer>,
        outputStream: OutputStream,
        width: Int,
        height: Int
    ) = withContext(Dispatchers.IO) {
        val flattened = ImageFlattener.flattenLayers(context, layers, false, width, height)
            ?: throw IllegalStateException("Failed to flatten layers for BMP")

        val pixels = IntArray(width * height)
        flattened.getPixels(pixels, 0, width, 0, 0, width, height)
        flattened.recycle()

        val imageSize = width * height * 4
        val fileSize = 14 + 40 + imageSize

        // 1. FILE HEADER (14 bytes)
        val fileHeader = ByteBuffer.allocate(14).order(ByteOrder.LITTLE_ENDIAN)
        fileHeader.put(0x42.toByte()) // B
        fileHeader.put(0x4D.toByte()) // M
        fileHeader.putInt(fileSize)
        fileHeader.putShort(0) // Reserved 1
        fileHeader.putShort(0) // Reserved 2
        fileHeader.putInt(54)  // Offset to pixel data
        outputStream.write(fileHeader.array())

        // 2. DIB HEADER (BITMAPINFOHEADER - 40 bytes)
        val dibHeader = ByteBuffer.allocate(40).order(ByteOrder.LITTLE_ENDIAN)
        dibHeader.putInt(40)     // Header size
        dibHeader.putInt(width)
        dibHeader.putInt(height) // Positive height = bottom-to-top
        dibHeader.putShort(1)    // Planes
        dibHeader.putShort(32)   // Bit count (RGBA)
        dibHeader.putInt(0)      // Compression (0 = BI_RGB)
        dibHeader.putInt(imageSize)
        dibHeader.putInt(0)      // X pixels per meter
        dibHeader.putInt(0)      // Y pixels per meter
        dibHeader.putInt(0)      // Colors used
        dibHeader.putInt(0)      // Important colors
        outputStream.write(dibHeader.array())

        // 3. PIXEL DATA (Bottom-to-top, BGRA)
        val rowBuffer = ByteBuffer.allocate(width * 4).order(ByteOrder.LITTLE_ENDIAN)
        for (y in (height - 1) downTo 0) {
            rowBuffer.clear()
            for (x in 0 until width) {
                val p = pixels[y * width + x]
                rowBuffer.put((p and 0xFF).toByte())         // Blue
                rowBuffer.put(((p shr 8) and 0xFF).toByte())  // Green
                rowBuffer.put(((p shr 16) and 0xFF).toByte()) // Red
                rowBuffer.put(((p shr 24) and 0xFF).toByte()) // Alpha
            }
            outputStream.write(rowBuffer.array())
        }

        outputStream.flush()
    }
}
