package com.example.apexphotolab.working_project.header

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * Job: Logic / Side-Effect.
 * Manages the "Time Since Saved" ticking loop and string formatting.
 */
@Composable
fun rememberSaveTimerState(lastSaveTime: Long): SaveTimerState {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Live update loop for the timer
    LaunchedEffect(lastSaveTime) {
        currentTime = System.currentTimeMillis()
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    return remember(currentTime, lastSaveTime) {
        val elapsedMillis = if (lastSaveTime == 0L) 0L else currentTime - lastSaveTime
        val seconds = (elapsedMillis / 1000) % 60
        val minutes = (elapsedMillis / 1000) / 60
        
        val timerText = if (lastSaveTime == 0L) {
            "Not Saved" 
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
        
        val isOverdue = minutes >= 5
        
        SaveTimerState(text = timerText, isOverdue = isOverdue)
    }
}

data class SaveTimerState(
    val text: String,
    val isOverdue: Boolean
)
