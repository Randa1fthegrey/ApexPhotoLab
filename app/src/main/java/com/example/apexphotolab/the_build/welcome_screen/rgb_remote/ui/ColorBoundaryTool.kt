package com.example.apexphotolab.the_build.welcome_screen.rgb_remote.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog

/**
 * Job: Color Boundary Tool Dialog.
 * Responsibility: Hosting the legacy ColorWheelView in a Compose Dialog and showing Hex Toasts.
 */
@Composable
fun ColorBoundaryTool(onDismiss: () -> Unit) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.size(400.dp),
            tonalElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                AndroidView(
                    factory = { ctx: Context ->
                        ColorWheelView(ctx).apply {
                            setOnColorSelectedListener { color ->
                                val hex = String.format("#%06X", 0xFFFFFF and color)
                                Toast.makeText(context, "Boundary Hex: $hex", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
