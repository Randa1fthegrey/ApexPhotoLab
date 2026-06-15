package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers

import android.content.Context
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.util.ImageFlattener
import com.example.apexphotolab.the_build.working_project.util.layers.Layer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Job: BMP File Saver.
 * Responsibility: Manually writing a flattened bitmap to the BMP format.
 * Note: Implements a 32-bit BGRA uncompressed BMP (V3 BITMAPINFOHEADER) per the Microsoft BMP File Format Specification.
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
        val fileSize = val_util.BMP_HEADER_SIZE + val_util.BMP_DIB_SIZE + imageSize

        // 1. FILE HEADER (14 bytes)
        val fileHeader = ByteBuffer.allocate(val_util.BMP_HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN)
        fileHeader.put(val_util.BMP_SIG_B) // B
        fileHeader.put(val_util.BMP_SIG_M) // M
        fileHeader.putInt(fileSize)
        fileHeader.putShort(0) // Reserved 1
        fileHeader.putShort(0) // Reserved 2
        fileHeader.putInt(val_util.BMP_DATA_OFFSET)  // Offset to pixel data
        outputStream.write(fileHeader.array())

        // 2. DIB HEADER (BITMAPINFOHEADER - 40 bytes)
        val dibHeader = ByteBuffer.allocate(val_util.BMP_DIB_SIZE).order(ByteOrder.LITTLE_ENDIAN)
        dibHeader.putInt(val_util.BMP_DIB_SIZE)     // Header size
        dibHeader.putInt(width)
        dibHeader.putInt(height) // Positive height = bottom-to-top
        dibHeader.putShort(val_util.BMP_PLANES)    // Planes
        dibHeader.putShort(val_util.BMP_BIT_COUNT_RGBA)   // Bit count (RGBA)
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
