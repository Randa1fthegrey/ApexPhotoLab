package com.example.apexphotolab.working_project.tool_panel.export.data

/**
 * A simple, shared enum to define the available export formats.
 * Updated: Professional Suite expansion with specialized WebP types.
 */
enum class ExportType { 
    PNG, SVG, JPG, 
    WEBP_LOSSY, WEBP_LOSSLESS, 
    BMP, PSD, TIFF, XCF 
}
