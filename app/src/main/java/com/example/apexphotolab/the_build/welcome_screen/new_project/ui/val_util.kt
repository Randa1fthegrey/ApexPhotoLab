package com.example.apexphotolab.the_build.welcome_screen.new_project.ui

/**
 * New Project UI Value Utility.
 * Responsibility: Centralizing strings and constants for the New Project wizard UI.
 */
object val_util {

    // ==========================================
    // SHARED BUTTONS
    // ==========================================

    val BUTTON_CONTINUE = "Continue" // *
    val BUTTON_CANCEL = "Cancel" // *

    // ==========================================
    // COPY CONFIRM DIALOG
    // ==========================================

    val COPY_TITLE = "Create New Project?"
    val COPY_MESSAGE_START = "This will copy the selected image into a new project named \""
    val COPY_MESSAGE_END = "\". The original image will not be affected. Continue?"

    // ==========================================
    // PROJECT NAME DIALOG
    // ==========================================

    val NAME_TITLE = "Name Your Project"
    val NAME_LABEL = "Project Name"

    // ==========================================
    // PROJECT TYPE DIALOG
    // ==========================================

    val TYPE_TITLE = "Select Project Type"
    val TYPE_STATIC = "Static / PNG (Single)"
    val TYPE_ANIMATED = "Animated / GIF (Motion)"
    val TYPE_SEQUENTIAL = "Sequential / PDF (Document)"
}
