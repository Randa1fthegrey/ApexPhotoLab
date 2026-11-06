package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.export

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.export.NesRawImageExtractor
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.flattening.NesAspectCropper
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.flattening.NesCanvasRecalibrator
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting.NesAttributeBlockQuantizer
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting.NesBackgroundQuantizer
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting.NesChrEncoder
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting.NesTileDeduplicator
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting.NesTileSlicer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers.TextFileSaver
import com.example.apexphotolab.the_build.working_project.util.layers.Layer

/**
 * Job: NES Java Blueprint Export Manager.
 * Responsibility: Pipeline orchestrator that processes image layers and exports
 * the complete NES_BIOS_Blueprint_Data.java source code file.
 */
object NesBlueprintExportManager {

    suspend fun exportJavaBlueprint(
        context: Context,
        layers: List<Layer>,
        outputUri: Uri
    ) {
        val rawBitmap = NesRawImageExtractor.extractRawBaseImage(context, layers) ?: return

        try {
            // 1. Center-crop high-res image to perfect 4:3 NES Aspect Ratio (prevents letterboxing & stretching)
            val croppedBitmap = NesAspectCropper.cropToNesAspect(rawBitmap)

            // 2. Shrink to exact NES NTSC 256x240 display bounds (Bilinear filtering keeps lines smooth)
            val nesCanvas = NesCanvasRecalibrator.recalibrate(croppedBitmap)

            // 3. 13-Color BACKGROUND QUANTIZATION (Run on the final 256x240 canvas)
            // Finds Universal BG + 12 Accents and funnels every pixel into these 13 buckets
            val quantizedResult = NesBackgroundQuantizer.quantizeTo13Colors(nesCanvas)

            // 4. Enforce 8x8 MMC5 Attribute Block Rules and generate ExRAM attribute array
            val strictNesResult = NesAttributeBlockQuantizer.enforceAttributeBlocks(
                quantizedResult.bitmap13Color,
                universalBgColor = quantizedResult.active13Colors[0],
                master13Palette = quantizedResult.active13Colors
            )

            // 5. Slice 960 (32x30) 8x8 tiles from the strict NES canvas
            val rawTiles = NesTileSlicer.slice(strictNesResult)

            // 6. Deduplicate CHR tiles and Nametable tile IDs
            val dedupResult = NesTileDeduplicator.deduplicate(rawTiles)

            // 7. Encode 4,096-byte CHR Pattern Table
            val chrBytes = NesChrEncoder.encodePatternTable(dedupResult.uniqueTiles)

            // 8. Format 16-byte Palette (Building the 4 optimal sub-palettes)
            val paletteBytes = ByteArray(16)
            for (p in 0 until 4) {
                // Index 0 of every sub-palette is always the Universal Background
                paletteBytes[p * 4] = (quantizedResult.active13Colors[0] and 0xFF).toByte()
                // Fill remaining 3 slots with the accents (1..12)
                for (a in 1..3) {
                    val accentIdx = 1 + (p * 3) + (a - 1)
                    val colorCode = if (accentIdx < quantizedResult.active13Colors.size) {
                        quantizedResult.active13Colors[accentIdx]
                    } else {
                        quantizedResult.active13Colors[0] // Fallback to background
                    }
                    paletteBytes[p * 4 + a] = (colorCode and 0xFF).toByte()
                }
            }

            // 9. Format 1,024-byte Nametable + Attributes
            // Combine 960 Nametable Tiles
            val nametableBytes = ByteArray(1024)
            val nametableConverted = ByteArray(960)
            for (i in 0 until 960) {
                nametableConverted[i] = (dedupResult.nametableTileIds[i] and 0xFF).toByte()
            }
            System.arraycopy(nametableConverted, 0, nametableBytes, 0, 960)
            // Note: For Blueprint data with MMC5, ExRAM replaces standard attribute bytes, 
            // but we fill the remaining 64 bytes with 0 for backward compatibility with 1024-byte standard NAM.

            // 10. Generate .java source code string
            val javaCode = NesBlueprintJavaExporter.generateJavaClass(
                paletteBytes,
                nametableBytes,
                chrBytes
            )

            // 11. Save .java text file to target Uri
            TextFileSaver.saveText(context, javaCode, outputUri)

        } finally {
            rawBitmap.recycle()
        }
    }
}
