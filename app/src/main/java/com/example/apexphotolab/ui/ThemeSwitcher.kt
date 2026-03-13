package com.example.apexphotolab.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun ThemeSwitcher(label: String, checked: Boolean, onCheckedChange: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label)
        Switch(checked = checked, onCheckedChange = { onCheckedChange() })
    }
}
