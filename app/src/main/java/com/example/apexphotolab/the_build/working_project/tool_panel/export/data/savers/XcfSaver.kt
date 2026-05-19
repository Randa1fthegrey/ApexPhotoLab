package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.apexphotolab.the_build.working_project.util.layers.Layer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataOutputStream
import java.io.OutputStream

/**
 * Job: XCF File Saver.
 * Responsibility: Manually writing a layered bitmap to the GIMP XCF format.
 * Note: Implements a layered XCF v1 (32-bit RGBA) structure using Tiled Hybrid Streaming to support high-res (8k+) images.
 */
object XcfSaver {

    suspend fun saveLayeredXcf(
        context: Context,
        layers: List<Layer>,
        outputStream: OutputStream,
        canvasWidth: Int,
        canvasHeight: Int
    ) = withContext(Dispatchers.IO) {
        val dos = DataOutputStream(outputStream)

        // 1. Calculate Metadata and Offsets
        val layerInfos = layers.map { layer ->
            val bitmap = loadBitmap(context, layer.imageUri) ?: Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            )
            val info = calculateLayerSizes(layer, bitmap)
            XcfLayerContext(layer, bitmap, info)
        }

        // Header: 14 bytes
        // Image Props: 12 (Compression) + 8 (End) = 20 bytes
        // Pointer Table: (layers.size + 1) * 4 bytes
        // Channel Pointer: 4 bytes
        val baseOffset = 14L + 20L + ((layers.size + 1) * 4L) + 4L

        var currentOffset = baseOffset
        layerInfos.forEach { ctx ->
            ctx.startOffset = currentOffset
            currentOffset += ctx.sizes.totalSize
        }

        // 2. Write File Header
        dos.writeBytes("gimp xcf v001")
        dos.writeByte(0)
        dos.writeInt(canvasWidth)
        dos.writeInt(canvasHeight)
        dos.writeInt(0) // RGB

        // 3. Image Properties
        writeProperty(dos, 1, 4) { it.writeInt(0) } // No compression
        dos.writeInt(0); dos.writeInt(0) // PROP_END (8 bytes)

        // 4. Layer Pointer Table
        layerInfos.forEach { dos.writeInt(it.startOffset.toInt()) }
        dos.writeInt(0) // Terminator

        // 5. Channel Pointer Table (None)
        dos.writeInt(0)

        // 6. Write Layer Data
        layerInfos.forEach { ctx ->
            writeLayerContent(dos, ctx)
            ctx.bitmap.recycle()
        }

        dos.flush()
    }

    private class XcfLayerContext(val layer: Layer, val bitmap: Bitmap, val sizes: XcfSizes) {
        var startOffset: Long = 0
    }

    private data class XcfSizes(val totalSize: Long, val layerRecordSize: Int, val hierarchySize: Int, val levelSize: Int)

    private const val TILE_WIDTH = 64
    private const val TILE_HEIGHT = 64

    private fun calculateLayerSizes(layer: Layer, bitmap: Bitmap): XcfSizes {
        val w = bitmap.width
        val h = bitmap.height
        val nameBytes = 4 + layer.title.toByteArray(Charsets.UTF_8).size + 1

        // Prop size: Opacity(12), Visible(12), Offsets(16), End(8) = 48
        val propSize = 12 + 12 + 16 + 8
        val layerRecordSize = 12 + nameBytes + propSize + 8 // 12 (Dim/Type) + Name + Props + 8 (Pointers)

        val hierarchySize = 12 + 8 // 12 (Dim/BPP) + 8 (Pointers)

        val tilesX = (w + 63) / 64
        val tilesY = (h + 63) / 64
        val tileCount = tilesX * tilesY
        val levelSize = 8 + (tileCount + 1) * 4 // 8 (Dim) + Pointers + End

        val tileDataSize = tileCount.toLong() * TILE_WIDTH * TILE_HEIGHT * 4

        val totalSize = layerRecordSize + hierarchySize + levelSize + tileDataSize
        return XcfSizes(totalSize, layerRecordSize, hierarchySize, levelSize)
    }

    private fun writeLayerContent(dos: DataOutputStream, ctx: XcfLayerContext) {
        val w = ctx.bitmap.width
        val h = ctx.bitmap.height

        // --- LAYER RECORD ---
        dos.writeInt(w)
        dos.writeInt(h)
        dos.writeInt(1) // RGBA_GIMP
        writeString(dos, ctx.layer.title)

        writeProperty(dos, 6, 4) { it.writeInt(255) }
        writeProperty(dos, 7, 4) { it.writeInt(if (ctx.layer.isVisible) 1 else 0) }
        writeProperty(dos, 15, 8) {
            it.writeInt(ctx.layer.xPosition.toInt())
            it.writeInt(ctx.layer.yPosition.toInt())
        }
        dos.writeInt(0); dos.writeInt(0) // PROP_END

        val hierarchyOffset = ctx.startOffset + ctx.sizes.layerRecordSize
        dos.writeInt(hierarchyOffset.toInt())
        dos.writeInt(0) // Mask pointer

        // --- HIERARCHY ---
        dos.writeInt(w)
        dos.writeInt(h)
        dos.writeInt(4) // BPP (RGBA)
        val levelOffset = hierarchyOffset + ctx.sizes.hierarchySize
        dos.writeInt(levelOffset.toInt())
        dos.writeInt(0) // End levels

        // --- LEVEL ---
        dos.writeInt(w)
        dos.writeInt(h)
        val tilesX = (w + 63) / 64
        val tilesY = (h + 63) / 64
        val tileCount = tilesX * tilesY
        val tileDataStart = levelOffset + ctx.sizes.levelSize

        for (i in 0 until tileCount) {
            dos.writeInt((tileDataStart + (i.toLong() * TILE_WIDTH * TILE_HEIGHT * 4)).toInt())
        }
        dos.writeInt(0) // End tiles

        // --- TILE DATA (Hybrid Streaming) ---
        val tileBuffer = IntArray(TILE_WIDTH * TILE_HEIGHT)
        for (ty in 0 until tilesY) {
            for (tx in 0 until tilesX) {
                val tw = if ((tx + 1) * TILE_WIDTH <= w) TILE_WIDTH else w - (tx * TILE_WIDTH)
                val th = if ((ty + 1) * TILE_HEIGHT <= h) TILE_HEIGHT else h - (ty * TILE_HEIGHT)

                ctx.bitmap.getPixels(tileBuffer, 0, TILE_WIDTH, tx * TILE_WIDTH, ty * TILE_HEIGHT, tw, th)

                for (y in 0 until TILE_HEIGHT) {
                    for (x in 0 until TILE_WIDTH) {
                        if (x < tw && y < th) {
                            val p = tileBuffer[y * TILE_WIDTH + x]
                            dos.writeByte((p shr 16) and 0xFF) // R
                            dos.writeByte((p shr 8) and 0xFF)  // G
                            dos.writeByte(p and 0xFF)         // B
                            dos.writeByte((p shr 24) and 0xFF) // A
                        } else {
                            repeat(4) { dos.writeByte(0) }
                        }
                    }
                }
            }
        }
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

    private fun writeProperty(dos: DataOutputStream, type: Int, length: Int, block: (DataOutputStream) -> Unit) {
        dos.writeInt(type)
        dos.writeInt(length)
        block(dos)
    }

    private fun writeString(dos: DataOutputStream, str: String) {
        val bytes = str.toByteArray(Charsets.UTF_8)
        dos.writeInt(bytes.size + 1)
        dos.write(bytes)
        dos.writeByte(0)
    }
}
