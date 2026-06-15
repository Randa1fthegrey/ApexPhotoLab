package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.savers

/**
 * Export Savers Value Utility.
 * Responsibility: Centralizing binary headers, offsets, and magic numbers for file format encoders.
 */
object val_util {

    // ==========================================
    // BMP SPECIFICATION (Microsoft V3)
    // ==========================================

    val BMP_SIG_B = 0x42.toByte()
    val BMP_SIG_M = 0x4D.toByte()
    val BMP_HEADER_SIZE = 14
    val BMP_DIB_SIZE = 40
    val BMP_DATA_OFFSET = 54
    val BMP_PLANES = 1.toShort()
    val BMP_BIT_COUNT_RGBA = 32.toShort()
}
