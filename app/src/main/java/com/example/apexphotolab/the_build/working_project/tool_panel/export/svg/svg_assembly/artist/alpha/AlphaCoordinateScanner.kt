package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.alpha

import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.vram.VRAM_EdgeFinder
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.svg_assembly.artist.AlphaGradientDetector
import java.nio.ByteBuffer

/**
 * Job: Alpha Coordinate Scanner.
 * Responsibility: Scanning a VRAM bitmask to identify edge coordinates of an alpha region.
 */
object AlphaCoordinateScanner {

    fun scan(vram: ByteBuffer, info: AlphaGradientDetector.AlphaGradientInfo): HashSet<Point> {
        return VRAM_EdgeFinder.findEdgesVRAM(vram, info.width, info.height)
    }
}