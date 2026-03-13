package com.example.apexphotolab.workspace.toolbars.export.svg.second_shift

import android.graphics.Point
import com.example.apexphotolab.workspace.toolbars.export.svg.second_shift.tracers.*
import java.nio.ByteBuffer

/**
 * Job #5: The Routing Specialist.
 * Acts as the switchboard to route work to the correct color-specialist tracer.
 * Updated: Now passes VRAM bitmasks to tracers to ensure zero-heap processing.
 */
object TracingRouter {

    /**
     * Routes the edges and VRAM bitmask to the specialized tracer.
     */
    fun route(index: Int, edges: HashSet<Point>, vram: ByteBuffer, width: Int): List<List<Point>> {
        return when (index) {
            0 -> RedPathTracer.trace(edges, vram, width)
            1 -> GreenPathTracer.trace(edges, vram, width)
            2 -> BluePathTracer.trace(edges, vram, width)
            3 -> YellowPathTracer.trace(edges, vram, width)
            4 -> CyanPathTracer.trace(edges, vram, width)
            5 -> MagentaPathTracer.trace(edges, vram, width)
            6 -> WhitePathTracer.trace(edges, vram, width)
            7 -> AlphaPathTracer.trace(edges, vram, width)
            8 -> BlackPathTracer.trace(edges, vram, width)
            9 -> GreyPathTracer.trace(edges, vram, width)
            else -> GreyPathTracer.trace(edges, vram, width)
        }
    }
}
