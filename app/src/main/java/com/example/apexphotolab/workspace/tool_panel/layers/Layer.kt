package com.example.apexphotolab.workspace.tool_panel.layers

import android.net.Uri

data class Layer(
    val id: String,
    var title: String,
    val imageUri: Uri,
    var isVisible: Boolean = true,
    var zOrder: Int = 0, // Stacking order
    var xPosition: Float = 0f,
    var yPosition: Float = 0f,
    var scale: Float = 1f,
    var rotation: Float = 0f
)
