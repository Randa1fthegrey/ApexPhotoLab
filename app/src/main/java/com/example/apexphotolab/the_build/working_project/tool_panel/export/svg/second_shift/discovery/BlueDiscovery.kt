package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.discovery
import android.graphics.Point
import com.example.apexphotolab.the_build.working_project.tool_panel.export.svg.second_shift.tracers.BluePathTracer
import java.nio.ByteBuffer
object BlueDiscovery {
    fun trace(edges: HashSet<Point>, vram: ByteBuffer, width: Int, pixels: IntArray, sharedRemainingSet: MutableSet<Point>? = null, specificCandidates: List<Point>? = null): List<List<Point>> {
        val candidates = specificCandidates ?: edges.sortedBy { it.y * 10000 + it.x }
        return BluePathTracer.trace(candidates, vram, width, pixels, sharedRemainingSet)
    }
}
