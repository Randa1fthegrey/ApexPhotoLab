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

            // 4. Enforce 16x16 Attribute Block Rules (Max 4 colors per 16x16 pixel block)
            val strictNesCanvas = NesAttributeBlockQuantizer.enforceAttributeBlocks(
                quantizedResult.bitmap13Color,
                universalBgColor = quantizedResult.active13Colors[0],
                master13Palette = quantizedResult.active13Colors
            )

            // 5. Slice 960 (32x30) 8x8 tiles from the strict NES canvas
            val rawTiles = NesTileSlicer.slice(strictNesCanvas)

            // 6. Deduplicate CHR tiles and Nametable tile IDs
            val dedupResult = NesTileDeduplicator.deduplicate(rawTiles)

            // 7. Encode 4,096-byte CHR Pattern Table
            val chrBytes = NesChrEncoder.encodePatternTable(dedupResult.uniqueTiles)

            // 8. Format 16-byte Palette
            val paletteBytes = byteArrayOf(
                0x0F, 0x31, 0x28, 0x30,  // Palette 0
                0x0F, 0x31, 0x28, 0x30,  // Palette 1
                0x0F, 0x31, 0x28, 0x30,  // Palette 2
                0x0F, 0x31, 0x28, 0x30   // Palette 3
            )

            // 9. Format 1,024-byte Nametable + Attributes
            val nametableBytes = ByteArray(1024)
            System.arraycopy(dedupResult.nametableTileIds, 0, nametableBytes, 0, 960)

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
