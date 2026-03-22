package com.example.apexphotolab.workspace.tool_panel.export.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A self-contained UI component for displaying the export progress.
 * Its only job is to show the dialog and the animations.
 */
@Composable
fun ExportProgressDialog(
    progress: Float,
    onCancel: () -> Unit
) {
    // This is the animation logic that was lost.
    val infiniteTransition = rememberInfiniteTransition(label = "loading_animation")
    val ballOffset by infiniteTransition.animateValue(
        initialValue = (-40).dp,
        targetValue = 40.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ball_offset"
    )

    AlertDialog(
        onDismissRequest = { /* Don't allow dismissal */ },
        title = { Text("Exporting Project") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(progress = { progress }, modifier = Modifier.size(64.dp))
                    Text("${(progress * 100).toInt()}%")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(x = ballOffset)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Your project is being exported to an SVG file. Please wait.")
            }
        },
        confirmButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    )
}
