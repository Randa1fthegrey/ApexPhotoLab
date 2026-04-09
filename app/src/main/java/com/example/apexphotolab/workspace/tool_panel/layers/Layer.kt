package com.example.apexphotolab.workspace.tool_panel.layers

import android.net.Uri

/**
 * The "Atomic Unit" of the Apex Photo Lab.
 * Pulls Triple Duty: Stacked Z-Order (Static), Frame Sequence (GIF), or Page Index (PDF).
 */
data class Layer(
    val id: String,
    var title: String,
    val imageUri: Uri,
    var isVisible: Boolean = true,
    var zOrder: Int = 0, // Z-Axis, Frame Index, or Page Number
    
    // Intrinsic Dimensions
    val width: Int = 0,
    val height: Int = 0,

    // Layout Intelligence
    var xPosition: Float = 0f,
    var yPosition: Float = 0f,
    var scale: Float = 1f,
    var rotation: Float = 0f,
    
    // Lock Intelligence
    var isLocked: Boolean = false,
    
    // Temporal Intelligence (for Animated Projects)
    var durationMs: Int = 100 // Duration of this frame in a sequence
)
