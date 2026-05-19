package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.net.Uri
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.util.ImageFlattener
import com.example.apexphotolab.the_build.working_project.util.layers.Layer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.OutputStream

/**
 * Job: PSD File Saver.
 * Responsibility: Manually writing a layered bitmap to the Adobe PSD format.
 * Note: Implements an 8-bit RGBA layered structure per the Adobe Photoshop File Formats Specification.
 */
object PsdLayeredSaver {

    private data class RenderedLayerInfo(
        val bitmap: Bitmap,
        val top: Int,
        val left: Int,
        val bottom: Int,
        val right: Int
    )

    suspend fun saveLayeredPsd(
        context: Context,
        layers: List<Layer>,
        outputStream: OutputStream,
        canvasWidth: Int,
        canvasHeight: Int
    ) = withContext(Dispatchers.IO) {
        val dos = DataOutputStream(outputStream)

        // 1. FILE HEADER (26 bytes)
        dos.writeBytes("8BPS") // Signature
        dos.writeShort(1)      // Version (1)
        dos.write(ByteArray(6)) // Reserved (6 bytes)
        dos.writeShort(4)      // Channels (R, G, B, A)
        dos.writeInt(canvasHeight)
        dos.writeInt(canvasWidth)
        dos.writeShort(8)      // Depth (8-bit)
        dos.writeShort(3)      // Color Mode (3 = RGB)

        // 2. COLOR MODE DATA SECTION
        dos.writeInt(0) // Length (0 for RGB)

        // 3. IMAGE RESOURCES SECTION
        // We'll provide a minimal "DisplayInfo" or just 0 for now to keep it simple
        dos.writeInt(0)

        // 4. LAYER AND MASK INFORMATION SECTION
        val layerData = buildLayerData(context, layers, canvasWidth, canvasHeight)

        // The section starts with a 4-byte length of the WHOLE section
        // But wait, the Layer Info itself has its own 4-byte length.
        val sectionLength = layerData.size + 4
        dos.writeInt(sectionLength)
        dos.writeInt(layerData.size)
        dos.write(layerData)

        // 5. IMAGE DATA SECTION (The "Composite" preview)
        // Photoshop requires a flattened version of the image at the end.
        val flattened =
            ImageFlattener.flattenLayers(context, layers, false, canvasWidth, canvasHeight)
        if (flattened != null) {
            writeImageData(dos, flattened)
            flattened.recycle()
        } else {
            dos.writeShort(0) // Compression: Raw
            dos.write(ByteArray(canvasWidth * canvasHeight * 4)) // Empty fallback
        }

        dos.flush()
    }

    private suspend fun buildLayerData(
        context: Context,
        layers: List<Layer>,
        canvasWidth: Int,
        canvasHeight: Int
    ): ByteArray {
        val baos = ByteArrayOutputStream()
        val dos = DataOutputStream(baos)

        // Layer Count (2 bytes) - Negative for standard transparency
        dos.writeShort(-layers.size)

        // 1. Render all layers first
        val renderedLayers = layers.map { layer ->
            renderLayerToBitmap(context, layer, canvasWidth, canvasHeight)
        }

        // 2. LAYER RECORDS
        renderedLayers.forEachIndexed { index, info ->
            val layer = layers[index]
            val bitmap = info.bitmap

            dos.writeInt(info.top)
            dos.writeInt(info.left)
            dos.writeInt(info.bottom)
            dos.writeInt(info.right)

            dos.writeShort(4) // Channels: R,G,B,A

            // Channel Info (ID [2], Length [4])
            // Raw data length = (W*H) + 2 for compression short
            val channelDataSize = (bitmap.width * bitmap.height) + 2
            for (id in listOf(0, 1, 2, -1)) { // R, G, B, Alpha
                dos.writeShort(id)
                dos.writeInt(channelDataSize)
            }

            dos.writeBytes("8BIM")
            dos.writeBytes("norm") // Blend Mode
            dos.writeByte(255)    // Opacity
            dos.writeByte(0)      // Clipping
            dos.writeByte(if (layer.isVisible) 0 else 2)
            dos.writeByte(0)      // Filler

            // Extra Data Size
            val nameBytes = layer.title.toByteArray(Charsets.UTF_8)
            val nameLength = nameBytes.size.coerceAtMost(255)
            val namePaddedSize = (nameLength + 1 + 3) and 3.inv()

            dos.writeInt(4 + 4 + namePaddedSize)
            dos.writeInt(0) // Mask Data
            dos.writeInt(0) // Blending Ranges

            // Name (Pascal String)
            dos.writeByte(nameLength)
            dos.write(nameBytes, 0, nameLength)
            // Padding to 4 bytes
            val padding = namePaddedSize - (nameLength + 1)
            repeat(padding) { dos.writeByte(0) }
        }

        // 3. CHANNEL IMAGE DATA
        renderedLayers.forEach { info ->
            val bitmap = info.bitmap
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

            // Write each channel in order R, G, B, A
            for (shift in listOf(16, 8, 0, 24)) {
                dos.writeShort(0) // No compression
                for (p in pixels) {
                    dos.writeByte((p shr shift) and 0xFF)
                }
            }
            bitmap.recycle()
        }

        return baos.toByteArray()
    }

    private suspend fun renderLayerToBitmap(context: Context, layer: Layer, canvasWidth: Int, canvasHeight: Int): RenderedLayerInfo {
        val original = loadBitmap(context, layer.imageUri) ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        // Calculate the transformed bounds
        val matrix = Matrix()
        matrix.postScale(layer.scale, layer.scale)
        matrix.postRotate(layer.rotation)

        // Get the bounds of the original bitmap after scale/rotate
        val srcRect = RectF(0f, 0f, original.width.toFloat(), original.height.toFloat())
        val dstRect = RectF()
        matrix.mapRect(dstRect, srcRect)

        val width = dstRect.width().toInt().coerceAtLeast(1)
        val height = dstRect.height().toInt().coerceAtLeast(1)

        // Create a bitmap for the transformed layer (just the size of the layer)
        val transformed = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(transformed)

        // Adjust matrix to draw within the new bitmap
        matrix.postTranslate(-dstRect.left, -dstRect.top)
        canvas.drawBitmap(original, matrix, null)
        original.recycle()

        // Final PSD coordinates (Top-Left of the layer on the GLOBAL canvas)
        val finalLeft = (layer.xPosition + dstRect.left).toInt()
        val finalTop = (layer.yPosition + dstRect.top).toInt()

        return RenderedLayerInfo(
            transformed,
            finalTop,
            finalLeft,
            finalTop + height,
            finalLeft + width
        )
    }

    private fun loadBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun writeImageData(dos: DataOutputStream, bitmap: Bitmap) {
        // PSD Image Data Section
        // Short: Compression (0 = Raw, 1 = RLE)
        // Then: Planar data (All Reds, then All Greens, then All Blues, then All Alphas)
        dos.writeShort(0) // Raw for now for maximum compatibility/simplicity

        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // Write R
        for (p in pixels) dos.writeByte((p shr 16) and 0xFF)
        // Write G
        for (p in pixels) dos.writeByte((p shr 8) and 0xFF)
        // Write B
        for (p in pixels) dos.writeByte(p and 0xFF)
        // Write A (Photoshop expects alpha too in a 4-channel file)
        for (p in pixels) dos.writeByte((p shr 24) and 0xFF)
    }
}
