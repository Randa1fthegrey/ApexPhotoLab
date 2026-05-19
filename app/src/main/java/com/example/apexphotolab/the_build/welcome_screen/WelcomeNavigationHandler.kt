package com.example.apexphotolab.the_build.welcome_screen

import com.example.apexphotolab.android.android_logic.app.AppState
import com.example.apexphotolab.android.android_keys.Screen

/**
 * Job: Welcome Navigation Handler.
 * Responsibility: Returns the application to the Welcome screen.
 */
class WelcomeNavigationHandler(private val appState: AppState) {
    fun execute() {
        appState.currentScreen.value = Screen.Welcome
    }
}
