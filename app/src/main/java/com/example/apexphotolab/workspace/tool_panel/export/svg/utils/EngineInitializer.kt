package com.example.apexphotolab.workspace.tool_panel.export.svg.utils

import android.graphics.Bitmap
import android.graphics.Point
import android.util.Log
import com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.final_assembly.SVGAssembler
import com.example.apexphotolab.workspace.tool_panel.export.svg.first_shift.color.ColorPalette
import com.example.apexphotolab.workspace.tool_panel.export.svg.first_shift.color.ColorRecorder
import com.example.apexphotolab.workspace.tool_panel.export.svg.first_shift.color.ValueClamper
import com.example.apexphotolab.workspace.tool_panel.export.svg.first_shift.ColorQuantizer
import com.example.apexphotolab.workspace.tool_panel.export.svg.first_shift.ColorSorter
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.tracers.*
import com.example.apexphotolab.workspace.tool_panel.export.svg.second_shift.SecondShiftOrchestrator
import com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist.AlphaFillGenerator
import com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist.AlphaGradientDetector
import com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist.AlphaSlopeAnalyzer
import com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist.GradientFillGenerator
import com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist.PathDataGenerator
import com.example.apexphotolab.workspace.tool_panel.export.svg.svg_assembly.artist.SolidFillGenerator
import com.example.apexphotolab.workspace.tool_panel.export.svg.first_shift.color.ramps.*
import com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.FloodFiller
import com.example.apexphotolab.workspace.tool_panel.export.svg.third_shift.SeedFinder
import kotlinx.coroutines.runBlocking

/**
 * The Ultimate Engine Roll Call.
 */
object EngineInitializer {

    private const val TAG = "EngineInitializer"

    fun warmUp() {
        Log.d(TAG, "Warming up SVG engine...")
        runBlocking {
            val dummyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

            // --- Data Layer ---
            val p1 = ColorPalette.PALETTE.size
            val p2 = ColorRecorder.PALETTE.size
            RedRamp.generate().size
            GreenRamp.generate().size
            BlueRamp.generate().size
            YellowRamp.generate().size
            CyanRamp.generate().size
            MagentaRamp.generate().size
            WhiteRamp.generate().size
            AlphaRamp.generate().size
            BlackRamp.generate().size
            GreyRamp.generate().size
            ValueClamper.apply(emptyList()).size
            SVGAssembler.assemble(emptyList(), 0, 0).length

            // --- Artist Tools ---
            PathDataGenerator.generate(emptyList())
            SolidFillGenerator.generate(emptyList(), 0)
            AlphaGradientDetector.detect(dummyBitmap)
            AlphaSlopeAnalyzer.analyze(hashSetOf(), IntArray(1), 1, 1)
            AlphaFillGenerator.generate(emptyList())
            
            val dummyGradientInfo = GradientFillGenerator.GradientInfo("dummy", 0, 0, GradientFillGenerator.GradientDirection.HORIZONTAL)
            GradientFillGenerator.generate(emptyList(), dummyGradientInfo)

            // --- Shift Tools & Orchestrators ---
            ColorQuantizer.quantize(dummyBitmap)
            ColorSorter.getNearestColor(0)
            SecondShiftOrchestrator.run(dummyBitmap)
            
            // Warm up all 10 specialized tracers with current signatures
            val dummyVram = VRAM_Garage.getSlotForManager(0)
            BlackPathTracer.trace(hashSetOf(), dummyVram, 1)
            RedPathTracer.trace(hashSetOf(), dummyVram, 1)
            GreenPathTracer.trace(hashSetOf(), dummyVram, 1)
            BluePathTracer.trace(hashSetOf(), dummyVram, 1)
            YellowPathTracer.trace(hashSetOf(), dummyVram, 1)
            CyanPathTracer.trace(hashSetOf(), dummyVram, 1)
            MagentaPathTracer.trace(hashSetOf(), dummyVram, 1)
            WhitePathTracer.trace(hashSetOf(), dummyVram, 1)
            AlphaPathTracer.trace(hashSetOf(), dummyVram, 1)
            GreyPathTracer.trace(hashSetOf(), dummyVram, 1)
            
            SeedFinder.findSeedPoint(emptyList(), dummyBitmap)
            FloodFiller.floodFill(Point(0,0), IntArray(1), 1, 1)

            // --- Core Infrastructure ---
            val c1 = CoreHighwayFactory.coreHighways.size
            val c2 = VRAM_Garage.getSlotForManager(1).capacity()

            Log.d(TAG, "Engine is hot! Palettes: $p1, $p2. Cores: $c1, VRAM: $c2")
        }
    }
}
