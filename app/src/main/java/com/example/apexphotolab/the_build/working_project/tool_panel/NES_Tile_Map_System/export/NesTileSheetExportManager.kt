package com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.export

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.export.NesRawImageExtractor
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.flattening.NesAspectCropper
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.flattening.NesCanvasRecalibrator
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting.NesAttributeBlockQuantizer
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting.NesBackgroundQuantizer
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting.NesFullGridTileMapRenderer
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting.NesTileDeduplicator
import com.example.apexphotolab.the_build.working_project.tool_panel.NES_Tile_Map_System.tile_slicing_sorting.NesTileSlicer
import com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers.BitmapFileSaver
import com.example.apexphotolab.the_build.working_project.util.layers.Layer

/**
 * Job: Universal NES Tile Map Export Manager.
 * Responsibility: Pipeline orchestrator that trims borders, enhances local adaptive contrast,
 * sanitizes colors with vector territory mapping, preserves line continuity, sharpens edges,
 * recalibrates to 256x240 NES NTSC resolution, slices into 8x8 tiles, deduplicates them,
 * and exports the full 960-tile (32x30) grid map image.
 */
object NesTileSheetExportManager {

    suspend fun exportTileMap(
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

            // 6. Deduplicate unique tiles into CHR table
            val dedupResult = NesTileDeduplicator.deduplicate(rawTiles)

            // 7. Generate full 960-tile (32x30) grid map image with grid lines & 4x scale (1024x960 px)
            val fullGridBitmap = NesFullGridTileMapRenderer.renderFullGridMap(
                dedupResult.uniqueTiles,
                dedupResult.nametableTileIds,
                scale = 4,
                drawGridLines = true
            )

            // 8. Save to target output URI via PhotoLab's standard PNG saver
            BitmapFileSaver.saveBitmap(
                context,
                fullGridBitmap,
                outputUri,
                Bitmap.CompressFormat.PNG,
                100
            )

        } finally {
            rawBitmap.recycle()
        }
    }
}
