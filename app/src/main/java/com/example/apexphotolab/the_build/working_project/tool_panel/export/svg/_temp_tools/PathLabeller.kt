package com.example.apexphotolab.the_build.working_project.tool_panel.export.svg._temp_tools

import android.graphics.Point

/**
 * Diagnostic tool to label every corner of a path with a unique number.
 * Helps identify exactly where the path tracer goes rogue.
 */
object PathLabeller {

    fun label(path: List<Point>, color: Int): String {
        if (!XRayControl.IS_XRAY_ENABLED || path.isEmpty()) return ""
        if (!XRayControl.isTargetColor(color)) return ""

        val labels = StringBuilder()
        val colorHex = String.format("#%06X", 0xFFFFFF and color)
        
        // Use the simplified corners for numbering to avoid cluttering every pixel
        val corners = simplify(path)

        corners.forEach { p ->
            if (p.x != -1) {
                val id = XRayControl.globalPointCounter++
                // Offset text slightly so it doesn't overlap the line
                labels.append("<text x=\"${p.x + 2}\" y=\"${p.y - 2}\" font-family=\"monospace\" font-size=\"6\" fill=\"$colorHex\" font-weight=\"bold\">$id</text>\n")
            }
        }

        return labels.toString()
    }

    private fun simplify(path: List<Point>): List<Point> {
        if (path.size <= 2) return path
        val simplified = mutableListOf<Point>()
        simplified.add(path[0])
        for (i in 1 until path.size - 1) {
            val prev = simplified.last()
            val curr = path[i]
            val next = path[i + 1]

            // Sentinel handling
            if (curr.x == -1 || prev.x == -1 || next.x == -1) {
                simplified.add(curr)
                continue
            }

            val dx1 = curr.x - prev.x
            val dy1 = curr.y - prev.y
            val dx2 = next.x - curr.x
            val dy2 = next.y - curr.y

            // If the cross product is non-zero, it's a corner
            val crossProduct = dy1 * dx2 - dy2 * dx1
            if (crossProduct != 0) {
                simplified.add(curr)
            }
        }
        simplified.add(path.last())
        return simplified
    }
}
