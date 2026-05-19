package com.example.apexphotolab.the_build.working_project.util.layers

import android.net.Uri

/**
 * Job: Atomic Unit (The "Brick").
 * Responsibility: Represents a single visual element on the canvas with its transform state.
 * 
 * Purified: Immutable data model. All state changes must be handled via .copy() 
 * to ensure Compose state signaling.
 */
data class Layer(
    val id: String,
    val title: String,
    val imageUri: Uri,
    val isVisible: Boolean = true,
    val zOrder: Int = 0,
    
    // Intrinsic Dimensions
    val width: Int = 0,
    val height: Int = 0,

    // Layout Intelligence
    val xPosition: Float = 0f,
    val yPosition: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    
    // Lock Intelligence
    val isLocked: Boolean = false
)
