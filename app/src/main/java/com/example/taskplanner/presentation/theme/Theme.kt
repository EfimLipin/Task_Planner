package com.example.taskplanner.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun PlannerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colors, content = content)
}