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
 * Job: TIFF File Saver.
 * Responsibility: Manually writing a flattened bitmap to the TIFF format.
 * Note: Implements a Baseline Little-Endian TIFF (8-bit RGBA) per the TIFF 6.0 Specification.
 */
object TiffSaver {

    suspend fun saveFlattenedTiff(
        context: Context,
        layers: List<Layer>,
        outputStream: OutputStream,
        width: Int,
        height: Int
    ) = withContext(Dispatchers.IO) {
        val flattened = ImageFlattener.flattenLayers(context, layers, false, width, height)
            ?: throw IllegalStateException("Failed to flatten layers for TIFF")

        val pixels = IntArray(width * height)
        flattened.getPixels(pixels, 0, width, 0, 0, width, height)
        flattened.recycle()

        // TIFF Header: 8 bytes
        // "II" (0x4949) for Little Endian, 42 (0x2A) for version, Offset to first IFD (8)
        val header = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
        header.putShort(0x4949.toShort())
        header.putShort(42)
        header.putInt(8)
        outputStream.write(header.array())

        // Image Data comes after the IFD. Let's calculate the IFD size.
        // Number of Directory Entries (2 bytes) + Entries (12 bytes each) + Next IFD Offset (4 bytes)
        val numEntries = 11
        val ifdSize = 2 + (numEntries * 12) + 4
        val imageDataOffset =
            8 + ifdSize + (3 * 2) // Extra space for BitsPerSample array (4 entries * 2 bytes)

        // IFD Entries
        val ifd = ByteBuffer.allocate(ifdSize).order(ByteOrder.LITTLE_ENDIAN)
        ifd.putShort(numEntries.toShort())

        writeTag(ifd, 256, 3, 1, width)           // ImageWidth
        writeTag(ifd, 257, 3, 1, height)          // ImageLength
        writeTag(ifd, 258, 3, 4, 8 + ifdSize)     // BitsPerSample (Points to data after IFD)
        writeTag(ifd, 259, 3, 1, 1)               // Compression (1 = None)
        writeTag(ifd, 262, 3, 1, 2)               // PhotometricInterpretation (2 = RGB)
        writeTag(ifd, 273, 4, 1, imageDataOffset) // StripOffsets
        writeTag(ifd, 277, 3, 1, 4)               // SamplesPerPixel (RGBA)
        writeTag(ifd, 278, 3, 1, height)          // RowsPerStrip
        writeTag(ifd, 279, 4, 1, width * height * 4) // StripByteCounts
        writeTag(ifd, 282, 5, 1, 0)               // XResolution (Optional, set to 0)
        writeTag(ifd, 283, 5, 1, 0)               // YResolution (Optional, set to 0)

        ifd.putInt(0) // Next IFD Offset (0 = None)
        outputStream.write(ifd.array())

        // BitsPerSample data (4 shorts: 8, 8, 8, 8)
        val bpsData = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
        bpsData.putShort(8)
        bpsData.putShort(8)
        bpsData.putShort(8)
        bpsData.putShort(8)
        outputStream.write(bpsData.array())

        // Image Data (Interleaved RGBA)
        val pixelBuffer = ByteBuffer.allocate(width * 4).order(ByteOrder.LITTLE_ENDIAN)
        for (y in 0 until height) {
            pixelBuffer.clear()
            for (x in 0 until width) {
                val p = pixels[y * width + x]
                pixelBuffer.put(((p shr 16) and 0xFF).toByte()) // R
                pixelBuffer.put(((p shr 8) and 0xFF).toByte())  // G
                pixelBuffer.put((p and 0xFF).toByte())         // B
                pixelBuffer.put(((p shr 24) and 0xFF).toByte()) // A
            }
            outputStream.write(pixelBuffer.array())
        }

        outputStream.flush()
    }

    private fun writeTag(buffer: ByteBuffer, tag: Int, type: Int, count: Int, value: Int) {
        buffer.putShort(tag.toShort())
        buffer.putShort(type.toShort())
        buffer.putInt(count)
        buffer.putInt(value)
    }
}
