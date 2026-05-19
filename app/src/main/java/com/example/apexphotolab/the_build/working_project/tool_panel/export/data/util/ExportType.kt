package com.example.apexphotolab.the_build.working_project.tool_panel.export.data.util

/**
 * Job: Export Format Definition.
 * Responsibility: Defining the available export format types as a shared contract.
 */
enum class ExportType {
    PNG, SVG, JPG,
    WEBP_LOSSY, WEBP_LOSSLESS,
    BMP, PSD, TIFF, XCF
}