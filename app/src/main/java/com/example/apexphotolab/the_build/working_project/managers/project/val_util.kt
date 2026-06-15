package com.example.apexphotolab.the_build.working_project.managers.project

/**
 * Project Manager Value Utility.
 * Responsibility: Centralizing file names, mime types, and reserved strings for project persistence.
 */
object val_util {

    // ==========================================
    // FILE NAMES & EXTENSIONS
    // ==========================================

    val FILE_LAYERS_JSON = "layers.json" // *
    val FILE_BASE_PNG = "base.png" // *
    val PREFIX_SAVE = "save_" // *
    val EXT_JSON = ".json" // *

    // ==========================================
    // MIME TYPES
    // ==========================================

    val MIME_JSON = "application/json" // *
    val MIME_PNG = "image/png" // *

    // ==========================================
    // RESERVED PROJECT STRINGS
    // ==========================================

    val SNAPSHOT_BIRTH = "Project Birth" // *
    val LAYER_BASE_ID = "base"
    val LAYER_BASE_TITLE = "Background"
}
