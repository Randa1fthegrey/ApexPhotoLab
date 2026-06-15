package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.util

/**
 * Export Utility Value Utility.
 * Responsibility: Centralizing default export dimensions and quality settings for orchestration.
 */
object val_util {

    // ==========================================
    // DEFAULT EXPORT PARAMETERS
    // ==========================================

    val DIMEN_DEFAULT = 1024
    val QUALITY_PNG = 100
    val QUALITY_JPG = 90
    val QUALITY_WEBP = 90

    // ==========================================
    // ERROR MESSAGES
    // ==========================================

    val ERR_BMP = "Could not open output stream for BMP"
    val ERR_TIFF = "Could not open output stream for TIFF"
    val ERR_PSD = "Could not open output stream for PSD"
    val ERR_XCF = "Could not open output stream for XCF"
}
