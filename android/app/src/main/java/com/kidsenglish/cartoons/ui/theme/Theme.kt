package com.kidsenglish.cartoons.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFFFF6B35),
    onPrimary = Color.White,
    secondary = Color(0xFF4ECDC4),
    onSecondary = Color.White,
    background = Color(0xFFFFF8F0),
    surface = Color.White,
    onBackground = Color(0xFF2D3436),
    onSurface = Color(0xFF2D3436)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF8A5C),
    onPrimary = Color.Black,
    secondary = Color(0xFF7EDEDA),
    background = Color(0xFF1A1A2E),
    surface = Color(0xFF16213E),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun KidsCartoonsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
