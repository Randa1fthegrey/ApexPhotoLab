package com.example.apexphotolab.the_build.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * UI Theme Value Utility.
 * Responsibility: Centralizing colors and typography for the application theme.
 */
object val_util {

    // ==========================================
    // COLOR PALETTE (Shared by Color.kt and Theme.kt)
    // ==========================================

    val Purple80 = Color(0xFFD0BCFF) // *
    val PurpleGrey80 = Color(0xFFCCC2DC) // *
    val Pink80 = Color(0xFFEFB8C8) // *

    val Purple40 = Color(0xFF6650a4) // *
    val PurpleGrey40 = Color(0xFF625b71) // *
    val Pink40 = Color(0xFF7D5260) // *

    // ==========================================
    // TYPOGRAPHY (Shared by Type.kt and Theme.kt)
    // ==========================================

    val Typography = Typography(
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        )
    ) // *
}
